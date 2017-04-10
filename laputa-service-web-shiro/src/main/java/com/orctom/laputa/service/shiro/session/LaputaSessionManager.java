package com.orctom.laputa.service.shiro.session;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.shiro.cookie.Cookie;
import com.orctom.laputa.service.shiro.cookie.SimpleCookie;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.DelegatingSession;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static com.orctom.laputa.service.shiro.util.RequestPairSourceUtils.getContext;
import static com.orctom.laputa.service.shiro.util.RequestPairSourceUtils.getRequestWrapper;

public class LaputaSessionManager extends DefaultSessionManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaputaSessionManager.class);

  private Cookie sessionIdCookie;
  private boolean sessionIdCookieEnabled;
  private boolean sessionIdUrlRewritingEnabled;

  public LaputaSessionManager() {
    Cookie cookie = new SimpleCookie(SimpleCookie.DEFAULT_SESSION_ID_NAME);
    cookie.setHttpOnly(true); //more secure, protects against XSS attacks
    cookie.setMaxAge(getMaxAge());
    this.sessionIdCookie = cookie;
    this.sessionIdCookieEnabled = true;
    this.sessionIdUrlRewritingEnabled = true;
  }

  private int getMaxAge() {
    long seconds = getGlobalSessionTimeout() / 1000;
    long intMaxValue = (long) Integer.MAX_VALUE;
    return intMaxValue < seconds ? Integer.MAX_VALUE : (int) seconds;
  }

  public Cookie getSessionIdCookie() {
    return sessionIdCookie;
  }

  public void setSessionIdCookie(Cookie sessionIdCookie) {
    this.sessionIdCookie = sessionIdCookie;
  }

  public boolean isSessionIdCookieEnabled() {
    return sessionIdCookieEnabled;
  }

  public void setSessionIdCookieEnabled(boolean sessionIdCookieEnabled) {
    this.sessionIdCookieEnabled = sessionIdCookieEnabled;
  }

  public boolean isSessionIdUrlRewritingEnabled() {
    return sessionIdUrlRewritingEnabled;
  }

  public void setSessionIdUrlRewritingEnabled(boolean sessionIdUrlRewritingEnabled) {
    this.sessionIdUrlRewritingEnabled = sessionIdUrlRewritingEnabled;
  }

  @Override
  protected Session createExposedSession(Session session, SessionContext sessionContext) {
    RequestWrapper requestWrapper = getRequestWrapper(sessionContext);
    Context context = getContext(sessionContext);
    SessionKey key = new LaputaSessionKey(session.getId(), requestWrapper, context);
    return new DelegatingSession(this, key);
  }

  @Override
  protected Session createExposedSession(Session session, SessionKey sessionKey) {
    RequestWrapper requestWrapper = getRequestWrapper(sessionKey);
    Context context = getContext(sessionKey);
    SessionKey key = new LaputaSessionKey(session.getId(), requestWrapper, context);
    return new DelegatingSession(this, key);
  }

  private void storeSessionId(Serializable currentId, RequestWrapper requestWrapper, Context context) {
    if (currentId == null) {
      String msg = "sessionId cannot be null when persisting for subsequent requests.";
      throw new IllegalArgumentException(msg);
    }
    Cookie template = getSessionIdCookie();
    Cookie cookie = new SimpleCookie(template);
    String idString = currentId.toString();
    cookie.setValue(idString);
    cookie.saveTo(requestWrapper, context);
    LOGGER.trace("Set session ID cookie for session with id {}", idString);
  }

  private void removeSessionIdCookie(RequestWrapper requestWrapper, Context context) {
    getSessionIdCookie().removeFrom(requestWrapper, context);
  }

  private String getSessionIdCookieValue(RequestWrapper requestWrapper, Context context) {
    if (!isSessionIdCookieEnabled()) {
      LOGGER.debug("Session ID cookie is disabled - session id will not be acquired from a request cookie.");
      return null;
    }
    return getSessionIdCookie().readValue(requestWrapper, context);
  }

  @Override
  protected void onExpiration(Session s, ExpiredSessionException ese, SessionKey key) {
    super.onExpiration(s, ese, key);
    onInvalidation(key);
  }

  @Override
  protected void onInvalidation(Session session, InvalidSessionException ise, SessionKey key) {
    super.onInvalidation(session, ise, key);
    onInvalidation(key);
  }

  private void onInvalidation(SessionKey key) {
    RequestWrapper requestWrapper = getRequestWrapper(key);
    Context context = getContext(key);
    removeSessionIdCookie(requestWrapper, context);
  }

  @Override
  protected void onStart(Session session, SessionContext sessionContext) {
    super.onStart(session, sessionContext);

    RequestWrapper requestWrapper = getRequestWrapper(sessionContext);
    Context context = getContext(sessionContext);

    if (isSessionIdCookieEnabled()) {
      Serializable sessionId = session.getId();
      storeSessionId(sessionId, requestWrapper, context);
    } else {
      LOGGER.debug("Session ID cookie is disabled.  No cookie has been set for new session with id {}", session.getId());
    }
  }

  @Override
  public Serializable getSessionId(SessionKey key) {
    Serializable id = super.getSessionId(key);
    if (null != id) {
      return id;
    }

    RequestWrapper requestWrapper = getRequestWrapper(key);
    Context context = getContext(key);
    return getSessionIdCookieValue(requestWrapper, context);
  }

  @Override
  protected void onStop(Session session, SessionKey key) {
    super.onStop(session, key);
    RequestWrapper requestWrapper = getRequestWrapper(key);
    Context context = getContext(key);
    removeSessionIdCookie(requestWrapper, context);
  }
}
