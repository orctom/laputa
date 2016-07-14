package com.orctom.utils.bean;

import com.orctom.utils.bean.model.Book;
import org.junit.rules.Stopwatch;

public class BeanUtilsTest {

  public void testJavaCalls() {
    Stopwatch sw = new Stopwatch();
    Book book = new Book();
//    book.setId(1L);
//    book.setAuthor("LuXun");
//    book.setName("SanWeiShuWu");
  }

  public void testNewInstance() {

  }

  public static void main(String[] args) {
    new BeanUtilsTest().testJavaCalls();
  }
}
