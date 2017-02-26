package com.orctom.laputa.service.util;

import com.google.common.collect.Lists;
import com.orctom.laputa.service.annotation.Param;
import com.orctom.laputa.service.config.Configurator;
import com.orctom.laputa.service.domain.Category;
import com.orctom.laputa.service.domain.Categories;
import com.orctom.laputa.service.domain.SKU;
import com.orctom.laputa.service.model.Context;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ArgResolverTest {

  @BeforeClass
  public static void beforeClass() {
    Configurator.getInstance();
  }

  static class Dummy {
    public void simple(@Param("a") String a,
                       @Param("b") String b,
                       @Param("c") String c) {
    }

    public void complex(@Param("category") Category category) {
    }

    public void evenMoreComplex(@Param("sku") SKU sku) {
    }

    public void mixed(@Param("a") String a,
                      @Param("b") String b,
                      @Param("category") Category category) {
    }

    public void indexed(@Param("skus") Categories categories) {
    }
  }

  @Test
  public void testSimpleTypes() throws Exception {
    Method method = Dummy.class.getDeclaredMethod("simple", String.class, String.class, String.class);
    Map<String, String> paramValues = new HashMap<>();
    paramValues.put("a", "aaa");
    paramValues.put("b", "bbb");
    paramValues.put("c", "ccc");

    Object[] expected = new Object[]{"aaa", "bbb", "ccc"};
    Object[] actual = ArgsResolver.resolveArgs(method.getParameters(), paramValues, new Context());
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testComplexTypes() throws Exception {
    Method method = Dummy.class.getDeclaredMethod("complex", Category.class);
    Map<String, String> paramValues = new HashMap<>();
    paramValues.put("id", "10000");
    paramValues.put("name", "the name");

    Object[] expected = new Object[]{new Category(10000L, "the name")};
    Object[] actual = ArgsResolver.resolveArgs(method.getParameters(), paramValues, new Context());
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testComplexTypes2() throws Exception {
    Method method = Dummy.class.getDeclaredMethod("complex", Category.class);
    Map<String, String> paramValues = new HashMap<>();
    paramValues.put("category.id", "10000");
    paramValues.put("category.name", "the name");

    Object[] expected = new Object[]{new Category(10000L, "the name")};
    Object[] actual = ArgsResolver.resolveArgs(method.getParameters(), paramValues, new Context());
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testEvenMoreComplexTypes() throws Exception {
    try {
      Method method = Dummy.class.getDeclaredMethod("evenMoreComplex", SKU.class);
      Map<String, String> paramValues = new HashMap<>();
      paramValues.put("id", "1000");
      paramValues.put("name", "sku name");
      paramValues.put("category.id", "1111");
      paramValues.put("category.name", "category name");

      Object[] expected = new Object[]{new SKU(1000L, "sku name", new Category(1111L, "category name"))};
      Object[] actual = ArgsResolver.resolveArgs(method.getParameters(), paramValues, new Context());
      assertArrayEquals(expected, actual);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testMixed() throws Exception {
    Method method = Dummy.class.getDeclaredMethod("mixed", String.class, String.class, Category.class);
    Map<String, String> paramValues = new HashMap<>();
    paramValues.put("a", "aaa");
    paramValues.put("b", "bbb");
    paramValues.put("category.id", "1000");
    paramValues.put("category.name", "category name");

    Object[] expected = new Object[]{"aaa", "bbb", new Category(1000L, "category name")};
    Object[] actual = ArgsResolver.resolveArgs(method.getParameters(), paramValues, new Context());
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testDate() throws Exception {
    Method method = Dummy.class.getDeclaredMethod("complex", Category.class);
    Map<String, String> paramValues = new HashMap<>();
    paramValues.put("id", "10000");
    paramValues.put("name", "the name");
    paramValues.put("date", "2016-09-09");

    Object[] expected = new Object[]{new Category(10000L, "the name")};
    Object[] actual = ArgsResolver.resolveArgs(method.getParameters(), paramValues, new Context());
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testIndexed() throws Exception {
    Method method = Dummy.class.getDeclaredMethod("indexed", Categories.class);
    Map<String, String> paramValues = new HashMap<>();
    paramValues.put("id", "10000");
    paramValues.put("categories[0].id", "10000");
    paramValues.put("categories[0].name", "the name");
    paramValues.put("categories[0].date", "2016-09-09");
    paramValues.put("categories[1].id", "10001");
    paramValues.put("categories[1].name", "the other name");
    paramValues.put("categories[1].date", "2016-09-10");

    Categories categories = new Categories();
    categories.setId("10000");
    categories.setCategories(Lists.newArrayList(
        new Category(10000L, "the name", DateTime.parse("2016-09-09").toDate()),
        new Category(10001L, "the other name", DateTime.parse("2016-09-10").toDate())
    ));
    Object[] expected = new Object[] {categories};
    Object[] actual = ArgsResolver.resolveArgs(method.getParameters(), paramValues, new Context());
    assertArrayEquals(expected, actual);
  }
}
