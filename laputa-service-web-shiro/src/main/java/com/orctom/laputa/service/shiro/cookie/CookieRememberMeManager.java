package com.orctom.laputa.service.shiro.cookie;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.AbstractRememberMeManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.orctom.laputa.service.shiro.util.RequestPairSourceUtils.getContext;
import static com.orctom.laputa.service.shiro.util.RequestPairSourceUtils.getRequestWrapper;

public class CookieRememberMeManager extends AbstractRememberMeManager {

  private static transient final Logger LOGGER = LoggerFactory.getLogger(CookieRememberMeManager.class);

  public static final String DEFAULT_REMEMBER_ME_COOKIE_NAME = "rememberMe";

  private Cookie cookie;

  public CookieRememberMeManager() {
    Cookie cookie = new SimpleCookie(DEFAULT_REMEMBER_ME_COOKIE_NAME);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(Cookie.TWO_WEEKS);
    this.cookie = cookie;
  }

  public Cookie getCookie() {
    return cookie;
  }

  public void setCookie(Cookie cookie) {
    this.cookie = cookie;
  }

  protected void rememberSerializedIdentity(Subject subject, byte[] serialized) {
    RequestWrapper requestWrapper = getRequestWrapper(subject);
    Context context = getContext(subject);

    //base 64 encode it and store as a cookie:
    String base64 = Base64.encodeToString(serialized);

    Cookie template = getCookie(); //the class attribute is really a template for the outgoing cookies
    Cookie cookie = new SimpleCookie(template);
    cookie.setValue(base64);
    cookie.saveTo(requestWrapper, context);
  }

  protected byte[] getRememberedSerializedIdentity(SubjectContext subjectContext) {
    RequestWrapper requestWrapper = getRequestWrapper(subjectContext);
    Context context = getContext(subjectContext);

    String base64 = getCookie().readValue(requestWrapper, context);
    if (Cookie.DELETED_COOKIE_VALUE.equals(base64)) {
      return null;
    }

    if (base64 != null) {
      base64 = ensurePadding(base64);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Acquired Base64 encoded identity [" + base64 + "]");
      }
      byte[] decoded = Base64.decode(base64);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Base64 decoded byte array length: " + (decoded != null ? decoded.length : 0) + " bytes.");
      }
      return decoded;
    } else {
      //no cookie set - new site visitor?
      return null;
    }
  }

  private String ensurePadding(String base64) {
    int length = base64.length();
    if (length % 4 != 0) {
      StringBuilder sb = new StringBuilder(base64);
      for (int i = 0; i < length % 4; ++i) {
        sb.append('=');
      }
      base64 = sb.toString();
    }
    return base64;
  }

  protected void forgetIdentity(Subject subject) {
    RequestWrapper requestWrapper = getRequestWrapper(subject);
    Context context = getContext(subject);
    forgetIdentity(requestWrapper, context);
  }

  public void forgetIdentity(SubjectContext subjectContext) {
    RequestWrapper requestWrapper = getRequestWrapper(subjectContext);
    Context context = getContext(subjectContext);
    forgetIdentity(requestWrapper, context);
  }

  private void forgetIdentity(RequestWrapper requestWrapper, Context context) {
    getCookie().removeFrom(requestWrapper, context);
  }
}

