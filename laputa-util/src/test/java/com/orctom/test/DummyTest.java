package com.orctom.test;

public class DummyTest {

  public static void main(String[] args) {
    String text = "include \"@projectName@-service-model\", \"@projectName@-service-web\", \"@projectName@-service-war\"";
    String regex = "(.*)@(\\w+)@(.*)";
    String replace = "$1\\$\\{$2\\}$3";
    System.out.println(text.replaceAll(regex, replace));
  }
}
