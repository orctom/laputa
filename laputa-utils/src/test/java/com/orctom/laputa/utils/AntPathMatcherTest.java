package com.orctom.laputa.utils;

import org.junit.Test;

import static com.orctom.laputa.utils.AntPathMatcher.matches;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class AntPathMatcherTest {

  @Test
  public void antStylePatternTest() {
    // test exact matching
    assertTrue(matches("test", "test"));
    assertTrue(matches("/test", "/test"));
    assertTrue(matches("http://example.org", "http://example.org"));
    assertFalse(matches("/test.jpg", "test.jpg"));
    assertFalse(matches("test", "/test"));
    assertFalse(matches("/test", "test"));

    // test matching with ?'s
    assertTrue(matches("t?st", "test"));
    assertTrue(matches("??st", "test"));
    assertTrue(matches("tes?", "test"));
    assertTrue(matches("te??", "test"));
    assertTrue(matches("?es?", "test"));
    assertFalse(matches("tes?", "tes"));
    assertFalse(matches("tes?", "testt"));
    assertFalse(matches("tes?", "tsst"));

    // test matching with *'s
    assertTrue(matches("*", "test"));
    assertTrue(matches("test*", "test"));
    assertTrue(matches("test*", "testTest"));
    assertTrue(matches("test/*", "test/Test"));
    assertTrue(matches("test/*", "test/t"));
    assertTrue(matches("test/*", "test/"));
    assertTrue(matches("*test*", "AnothertestTest"));
    assertTrue(matches("*test", "Anothertest"));
    assertTrue(matches("*.*", "test."));
    assertTrue(matches("*.*", "test.test"));
    assertTrue(matches("*.*", "test.test.test"));
    assertTrue(matches("test*aaa", "testblaaaa"));
    assertFalse(matches("test*", "tst"));
    assertFalse(matches("test*", "tsttest"));
    assertFalse(matches("test*", "test/"));
    assertFalse(matches("test*", "test/t"));
    assertFalse(matches("test/*", "test"));
    assertFalse(matches("*test*", "tsttst"));
    assertFalse(matches("*test", "tsttst"));
    assertFalse(matches("*.*", "tsttst"));
    assertFalse(matches("test*aaa", "test"));
    assertFalse(matches("test*aaa", "testblaaab"));

    // test matching with ?'s and /'s
    assertTrue(matches("/?", "/a"));
    assertTrue(matches("/?/a", "/a/a"));
    assertTrue(matches("/a/?", "/a/b"));
    assertTrue(matches("/??/a", "/aa/a"));
    assertTrue(matches("/a/??", "/a/bb"));
    assertTrue(matches("/?", "/a"));

    // test matching with **'s
    assertTrue(matches("/**", "/testing/testing"));
    assertTrue(matches("/*/**", "/testing/testing"));
    assertTrue(matches("/**/*", "/testing/testing"));
    assertTrue(matches("/bla/**/bla", "/bla/testing/testing/bla"));
    assertTrue(matches("/bla/**/bla", "/bla/testing/testing/bla/bla"));
    assertTrue(matches("/**/test", "/bla/bla/test"));
    assertTrue(matches("/bla/**/**/bla", "/bla/bla/bla/bla/bla/bla"));
    assertTrue(matches("/bla*bla/test", "/blaXXXbla/test"));
    assertTrue(matches("/*bla/test", "/XXXbla/test"));
    assertFalse(matches("/bla*bla/test", "/blaXXXbl/test"));
    assertFalse(matches("/*bla/test", "XXXblab/test"));
    assertFalse(matches("/*bla/test", "XXXbl/test"));

    assertFalse(matches("/????", "/bala/bla"));
    assertFalse(matches("/**/*bla", "/bla/bla/bla/bbb"));

    assertTrue(matches("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing/"));
    assertTrue(matches("/*bla*/**/bla/*", "/XXXblaXXXX/testing/testing/bla/testing"));
    assertTrue(matches("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing"));
    assertTrue(matches("/*bla*/**/bla/**", "/XXXblaXXXX/testing/testing/bla/testing/testing.jpg"));

    assertTrue(matches("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing/"));
    assertTrue(matches("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing"));
    assertTrue(matches("*bla*/**/bla/**", "XXXblaXXXX/testing/testing/bla/testing/testing"));
    assertFalse(matches("*bla*/**/bla/*", "XXXblaXXXX/testing/testing/bla/testing/testing"));

    assertFalse(matches("/x/x/**/bla", "/x/x/x/"));

    assertTrue(matches("/foo/bar/**", "/foo/bar"));

    assertTrue(matches("", ""));

    assertTrue(matches("/foo/bar/**", "/foo/bar"));
    assertTrue(matches("/resource/1", "/resource/1"));
    assertTrue(matches("/resource/*", "/resource/1"));
    assertTrue(matches("/resource/*/", "/resource/1/"));
    assertTrue(matches("/top-resource/*/resource/*/sub-resource/*", "/top-resource/1/resource/2/sub-resource/3"));
    assertTrue(matches("/top-resource/*/resource/*/sub-resource/*", "/top-resource/999999/resource/8888888/sub-resource/77777777"));
    assertTrue(matches("/*/*/*/*/secret.html", "/this/is/protected/path/secret.html"));
    assertTrue(matches("/*/*/*/*/*.html", "/this/is/protected/path/secret.html"));
    assertTrue(matches("/*/*/*/*", "/this/is/protected/path"));
    assertTrue(matches("org/springframework/**/*.jsp", "org/springframework/web/views/hello.jsp"));
    assertTrue(matches("org/springframework/**/*.jsp", "org/springframework/web/default.jsp"));
    assertTrue(matches("org/springframework/**/*.jsp", "org/springframework/default.jsp"));
    assertTrue(matches("org/**/servlet/bla.jsp", "org/springframework/servlet/bla.jsp"));
    assertTrue(matches("org/**/servlet/bla.jsp", "org/springframework/testing/servlet/bla.jsp"));
    assertTrue(matches("org/**/servlet/bla.jsp", "org/servlet/bla.jsp"));
    assertTrue(matches("**/hello.jsp", "org/springframework/servlet/web/views/hello.jsp"));
    assertTrue(matches("**/**/hello.jsp", "org/springframework/servlet/web/views/hello.jsp"));

    assertFalse(matches("/foo/bar/**", "/foo /bar"));
    assertFalse(matches("/foo/bar/**", "/foo          /bar"));
    assertFalse(matches("/foo/bar/**", "/foo          /               bar"));
    assertFalse(matches("/foo/bar/**", "       /      foo          /               bar"));
    assertFalse(matches("org/**/servlet/bla.jsp", "   org   /      servlet    /   bla   .   jsp"));
    
    // double asterisks
    assertTrue(matches("/static/**", "/static/a.jpg"));
    assertTrue(matches("/static/**", "/static/css/a.css"));
    assertTrue(matches("/static/**", "/static/js/a.js"));
    assertTrue(matches("/static/**", "/static/img/a.jpg"));
    assertTrue(matches("/static/**", "/static/a/b/c/d/e/f/g/a.jpg"));
    assertTrue(matches("/static/**", "/static"));
    assertTrue(matches("/static/**", "/static/"));

    // single asterisks
    assertTrue(matches("/static/*", "/static/a.jpg"));
    assertTrue(matches("/static/*", "/static/namkyuProfilePicture.jpg"));

    assertFalse(matches("/static/*", "/static/a/test.jpg"));
    assertFalse(matches("/static/*", "/static/a/b/c/d/test.jpg"));

    assertTrue(matches("/static*/*", "/static/test.jpg"));
    assertTrue(matches("/static*/*", "/static1/test.jpg"));
    assertTrue(matches("/static*/*", "/static123/test.jpg"));
    assertTrue(matches("/static*/*", "/static-123/test.jpg"));
    assertTrue(matches("/static*/*", "/static~!@#$%^&*()_+}{|/test.jpg"));

    assertFalse(matches("/static*/*", "/static12/a/test.jpg"));
    assertFalse(matches("/static*/*", "/static12/a/b/test.jpg"));

    // double and single combine
    assertTrue(matches("/static*/**", "/static/a.jpg"));
    assertTrue(matches("/static*/**", "/static1/a.jpg"));
    assertTrue(matches("/static*/**", "/static/a/a.jpg"));
    assertTrue(matches("/static*/**", "/static/a/b/a.jpg"));
    assertTrue(matches("/static*/**", "/static/a/b/c/a.jpg"));

    assertTrue(matches("**/static/**", "a/static/a/b/c/a.jpg"));
    assertTrue(matches("**/static/**", "a/b/static/a/b/c/a.jpg"));

    // question-mark
    assertTrue(matches("/static-?/**", "/static-a/a.jpg"));
    assertTrue(matches("/static-?/**", "/static-a/b/c/a.jpg"));
    assertTrue(matches("/static-?/*", "/static-a/abcd.jpg"));
    assertTrue(matches("/static-?/???.jpg", "/static-a/abc.jpg"));
  }
}
