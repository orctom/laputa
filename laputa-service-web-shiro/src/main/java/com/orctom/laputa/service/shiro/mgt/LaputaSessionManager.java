package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class LaputaSessionManager extends DefaultSessionManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(LaputaSessionManager.class);

  private Cookie sessionIdCookie;
  private boolean sessionIdCookieEnabled;
  private boolean sessionIdUrlRewritingEnabled;

  public LaputaSessionManager() {
    Cookie cookie = new SimpleCookie(SimpleCookie.DEFAULT_SESSION_ID_NAME);
    cookie.setHttpOnly(true); //more secure, protects against XSS attacks
    this.sessionIdCookie = cookie;
    this.sessionIdCookieEnabled = true;
    this.sessionIdUrlRewritingEnabled = true;
  }

  public Cookie getSessionIdCookie() {
    return sessionIdCookie;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void setSessionIdCookie(Cookie sessionIdCookie) {
    this.sessionIdCookie = sessionIdCookie;
  }

  public boolean isSessionIdCookieEnabled() {
    return sessionIdCookieEnabled;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void setSessionIdCookieEnabled(boolean sessionIdCookieEnabled) {
    this.sessionIdCookieEnabled = sessionIdCookieEnabled;
  }

  public boolean isSessionIdUrlRewritingEnabled() {
    return sessionIdUrlRewritingEnabled;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void setSessionIdUrlRewritingEnabled(boolean sessionIdUrlRewritingEnabled) {
    this.sessionIdUrlRewritingEnabled = sessionIdUrlRewritingEnabled;
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

  }
}
