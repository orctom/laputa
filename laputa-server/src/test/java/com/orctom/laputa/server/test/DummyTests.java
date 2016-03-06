package com.orctom.laputa.server.test;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class DummyTests {
  public static void main(String[] args) throws Exception {
//    DummyTests tests = new DummyTests();
//    tests.testInvoke();
    String url = "/product/hello/{name}";
    Pattern pattern = Pattern.compile("[\\{]");
    System.out.println(pattern.matcher(url).matches());
    System.out.println(url.matches("[\\w]"));
  }

  public void testCast() {
    int a = Integer.valueOf("234");
  }

  public void testInvoke() throws Exception {
    Method m = DummyTests.class.getMethod("foo", int.class);
    DummyTests target = new DummyTests();
    Class<?> type = m.getParameterTypes()[0];
    System.out.println(Integer.class.isAssignableFrom(type));
    System.out.println(int.class.isAssignableFrom(type));
    System.out.println(Double.class.isAssignableFrom(type));
    System.out.println(double.class.isAssignableFrom(type));
    m.invoke(target, Integer.valueOf("12345"));
  }

  public void foo(int x) {
    System.out.println("<<" + x + ">>");
  }
}
