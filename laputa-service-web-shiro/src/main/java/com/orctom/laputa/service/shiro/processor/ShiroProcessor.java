package com.orctom.laputa.service.shiro.processor;

import com.google.common.base.Strings;
import com.orctom.laputa.service.model.Context;
import com.orctom.laputa.service.model.RequestWrapper;
import com.orctom.laputa.service.processor.PreProcessor;
import com.orctom.laputa.service.shiro.filter.Filter;
import com.orctom.laputa.service.shiro.mgt.FilterChainResolver;
import com.orctom.laputa.service.shiro.mgt.NamedFilterList;
import com.orctom.laputa.service.shiro.subject.LaputaSubject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ListIterator;

import static com.orctom.laputa.service.shiro.ShiroContext.getFilterChainResolver;
import static com.orctom.laputa.service.shiro.ShiroContext.getSecurityManager;

@Component
public class ShiroProcessor implements PreProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShiroProcessor.class);

  @Override
  public int getOrder() {
    return 0;
  }

  @Override
  public void process(RequestWrapper requestWrapper, Context ctx) {
    final Subject subject = createSubject(requestWrapper, ctx);
    subject.execute(() -> {
      updateSessionLastAccessTime(requestWrapper, ctx);
      executeChain(requestWrapper, ctx);
    });
  }

  protected LaputaSubject createSubject(RequestWrapper requestWrapper, Context ctx) {
    return new LaputaSubject.Builder(getSecurityManager(), requestWrapper, ctx).buildSubject();
  }

  protected void updateSessionLastAccessTime(RequestWrapper requestWrapper, Context ctx) {
    Subject subject = SecurityUtils.getSubject();
    //Subject should never _ever_ be null, but just in case:
    if (subject != null) {
      Session session = subject.getSession(false);
      if (session != null) {
        try {
          session.touch();
        } catch (Throwable t) {
          LOGGER.error("session.touch() method invocation has failed.  Unable to update" +
              "the corresponding session's last access time based on the incoming request.", t);
        }
      }
    }
  }

  protected void executeChain(RequestWrapper requestWrapper, Context ctx) {
    NamedFilterList chain = getExecutionChain(requestWrapper, ctx);
    ListIterator<Filter> filters = chain.listIterator();
    while (filters.hasNext()) {
      Filter filter =  filters.next();
      filter.filter(requestWrapper, ctx);
      if (hasRedirection(ctx)) {
        return;
      }
    }
  }

  private NamedFilterList getExecutionChain(RequestWrapper requestWrapper, Context ctx) {
    FilterChainResolver resolver = getFilterChainResolver();
    return resolver.getChain(requestWrapper, ctx);
  }

  private boolean hasRedirection(Context ctx) {
    return !Strings.isNullOrEmpty(ctx.getRedirectTo());
  }
}
