package com.orctom.laputa.service.shiro.filter.authc;

import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
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
  protected void checkAccess(RequestWrapper requestWrapper, Context context, Object mappedValue) {
    if (isLoginRequest(requestWrapper)) {
      if (isLoginSubmission(requestWrapper, context)) {
        executeLogin(requestWrapper, context);
      }

    } else {
      saveRequestAndRedirectToLogin(requestWrapper, context);
    }
  }

  protected void executeLogin(RequestWrapper requestWrapper, Context context) {
    AuthenticationToken token = createToken(requestWrapper, context);
    if (token == null) {
      String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken " +
          "must be created in order to execute a login attempt.";
      throw new IllegalStateException(msg);
    }
    try {
      Subject subject = getSubject(requestWrapper);
      subject.login(token);
      onLoginSuccess(token, subject, requestWrapper, context);
    } catch (AuthenticationException e) {
      onLoginFailure(token, e, requestWrapper, context);
    }
  }

  protected boolean isLoginSubmission(RequestWrapper requestWrapper, Context context) {
    return HttpMethod.POST.equals(requestWrapper.getHttpMethod());
  }

  protected AuthenticationToken createToken(RequestWrapper requestWrapper, Context context) {
    String username = getUsername(requestWrapper);
    String password = getPassword(requestWrapper);
    boolean rememberMe = isRememberMe(requestWrapper);
    return createToken(username, password, rememberMe, null);
  }

  protected AuthenticationToken createToken(String username, String password,
                                            boolean rememberMe, String host) {
    return new UsernamePasswordToken(username, password, rememberMe, host);
  }

  protected boolean isRememberMe(RequestWrapper requestWrapper) {
    List<String> values = requestWrapper.getParams().get(getRememberMeParam());
    if (null == values || values.isEmpty()) {
      return false;
    }
    return Booleans.isBooleam(values.get(0));
  }

  protected void onLoginSuccess(AuthenticationToken token,
                                   Subject subject,
                                   RequestWrapper requestWrapper,
                                   Context context) {
    issueSuccessRedirect(requestWrapper, context);
  }

  protected void onLoginFailure(AuthenticationToken token,
                                   AuthenticationException e,
                                   RequestWrapper requestWrapper,
                                   Context context) {
    context.setRedirectTo(getLoginUrl() + "?error=Login failed.");
    context.setData("error", e.getMessage());
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
