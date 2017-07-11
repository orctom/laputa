package com.orctom.laputa.utils;

import org.junit.*;

import static org.junit.Assert.*;

public class URLUtilsTest {

  @Test
  public void removeDoubleSlashes() throws Exception {
    String expected = "template/path";
    String actual = URLUtils.removeDoubleSlashes("template//path");
    Assert.assertEquals(expected, actual);
  }

}