package com.orctom.laputa.service.shiro.filter.authc;

import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.model.ResponseWrapper;
import com.orctom.laputa.utils.Booleans;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FormAuthenticationFilter extends AuthenticationFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(FormAuthenticationFilter.class);

  public static final String DEFAULT_USERNAME_PARAM = "username";
  public static final String DEFAULT_PASSWORD_PARAM = "password";

  public static final String DEFAULT_REMEMBER_ME_PARAM = "rememberMe";

  private String usernameParam = DEFAULT_USERNAME_PARAM;
  private String passwordParam = DEFAULT_PASSWORD_PARAM;
  private String rememberMeParam = DEFAULT_REMEMBER_ME_PARAM;

  public FormAuthenticationFilter() {
    setLoginUrl("/login.html");
  }

  public String getUsernameParam() {
    return usernameParam;
  }

  public void setUsernameParam(String usernameParam) {
    this.usernameParam = usernameParam;
  }

  public String getPasswordParam() {
    return passwordParam;
  }

  public void setPasswordParam(String passwordParam) {
    this.passwordParam = passwordParam;
  }

  public String getRememberMeParam() {
    return rememberMeParam;
  }

  public void setRememberMeParam(String rememberMeParam) {
    this.rememberMeParam = rememberMeParam;
  }

  @Override
  public void setLoginUrl(String loginUrl) {
    String previous = getLoginUrl();
    if (previous != null) {
      this.appliedPaths.remove(previous);
    }
    super.setLoginUrl(loginUrl);
    this.appliedPaths.put(getLoginUrl(), null);
  }

  @Override
  protected boolean onAccessDenied(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    if (isLoginRequest(requestWrapper)) {
      if (isLoginSubmission(requestWrapper, responseWrapper)) {
        return executeLogin(requestWrapper, responseWrapper);
      } else {
        return true;
      }

    } else {
      saveRequestAndRedirectToLogin(requestWrapper, responseWrapper);
      return false;
    }
  }

  protected boolean executeLogin(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    AuthenticationToken token = createToken(requestWrapper, responseWrapper);
    if (token == null) {
      String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken " +
          "must be created in order to execute a login attempt.";
      throw new IllegalStateException(msg);
    }
    
    try {
      Subject subject = getSubject(requestWrapper);
      subject.login(token);
      return onLoginSuccess(token, subject, requestWrapper, responseWrapper);
    } catch (AuthenticationException e) {
      return onLoginFailure(token, e, requestWrapper, responseWrapper);
    }
  }

  protected boolean isLoginSubmission(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    return HttpMethod.POST.equals(requestWrapper.getHttpMethod());
  }

  protected AuthenticationToken createToken(RequestWrapper requestWrapper, ResponseWrapper responseWrapper) {
    String username = getUsername(requestWrapper);
    String password = getPassword(requestWrapper);
    boolean rememberMe = isRememberMe(requestWrapper);
    return createToken(username, password, rememberMe, null);
  }

  protected AuthenticationToken createToken(String username,
                                            String password,
                                            boolean rememberMe,
                                            String host) {
    return new UsernamePasswordToken(username, password, rememberMe, host);
  }

  protected boolean isRememberMe(RequestWrapper requestWrapper) {
    List<String> values = requestWrapper.getParams().get(getRememberMeParam());
    if (null == values || values.isEmpty()) {
      return false;
    }
    return Booleans.isBooleam(values.get(0));
  }

  protected boolean onLoginSuccess(AuthenticationToken token,
                                   Subject subject,
                                   RequestWrapper requestWrapper,
                                   ResponseWrapper responseWrapper) {
    issueSuccessRedirect(requestWrapper, responseWrapper);
    return false;
  }

  protected boolean onLoginFailure(AuthenticationToken token,
                                   AuthenticationException e,
                                   RequestWrapper requestWrapper,
                                   ResponseWrapper responseWrapper) {
    responseWrapper.setRedirectTo(getLoginUrl() + "?error=Login failed.");
    responseWrapper.setData("error", e.getMessage());
    return false;
  }

  protected String getUsername(RequestWrapper requestWrapper) {
    List<String> values = requestWrapper.getParams().get(getUsernameParam());
    if (null == values || values.isEmpty()) {
      return null;
    }
    return values.get(0);
  }

  protected String getPassword(RequestWrapper requestWrapper) {
    List<String> values = requestWrapper.getParams().get(getPasswordParam());
    if (null == values || values.isEmpty()) {
      return null;
    }
    return values.get(0);
  }
}
