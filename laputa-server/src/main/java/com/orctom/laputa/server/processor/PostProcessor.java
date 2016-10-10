package com.orctom.laputa.server.processor;

/**
 * processors before translate the Java object to endpoint (json, xml, html...)
 * They will be process by order of <code>getOrder()</code> ASC
 * Created by chenhao on 9/27/16.
 */
public interface PostProcessor {

  int getOrder();

  Object process(Object data);
}
