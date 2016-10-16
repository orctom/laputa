package com.orctom.laputa.utils;

import com.orctom.laputa.exception.ClassLoadingException;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * Class Utils
 * Created by hao-chen2 on 1/5/2015.
 */
public class ClassUtils {

  private static final Set<Class<?>> primitiveWrapperTypes = new HashSet<>(8);

  static {
    primitiveWrapperTypes.add(Boolean.class);
    primitiveWrapperTypes.add(Byte.class);
    primitiveWrapperTypes.add(Character.class);
    primitiveWrapperTypes.add(Double.class);
    primitiveWrapperTypes.add(Float.class);
    primitiveWrapperTypes.add(Integer.class);
    primitiveWrapperTypes.add(Long.class);
    primitiveWrapperTypes.add(Short.class);
  }

  /**
   * @param packageName     The base package
   * @param annotationClass The annotation class
   * @return The classes
   * @throws ClassLoadingException
   */
  public static List<Class<?>> getClassesWithAnnotation(String packageName, Class<? extends Annotation> annotationClass)
      throws ClassLoadingException {
    List<Class<?>> classes = getClasses(packageName);
    List<Class<?>> clazzes = new ArrayList<>();
    for (Class<?> clazz : classes) {
      if (clazz.isAnnotationPresent(annotationClass)) {
        clazzes.add(clazz);
      }
    }
    return clazzes;
  }

  /**
   * Scans all classes accessible from the context class loader which belong
   * to the given package and subpackages.
   *
   * @param packageName The base package
   * @return The classes
   * @throws ClassLoadingException
   */
  public static List<Class<?>> getClasses(String packageName) throws ClassLoadingException {
    List<Class<?>> classes;
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      assert classLoader != null;
      String path = packageName.replace('.', '/');
      Enumeration<URL> resources = classLoader.getResources(path);
      List<File> dirs = new ArrayList<>();
      while (resources.hasMoreElements()) {
        URL resource = resources.nextElement();
        String fileName = resource.getFile();
        String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
        dirs.add(new File(fileNameDecoded));
      }
      classes = new ArrayList<>();
      for (File directory : dirs) {
        classes.addAll(findClasses(directory, packageName));
      }
    } catch (IOException e) {
      throw new ClassLoadingException(e);
    }
    return classes;
  }

  /**
   * Recursive method used to find all classes in a given directory and
   * subdirs.
   *
   * @param directory   The base directory
   * @param packageName The package name for classes found inside the base directory
   * @return The classes
   * @throws ClassLoadingException
   */
  private static List<Class<?>> findClasses(File directory, String packageName) throws ClassLoadingException {
    List<Class<?>> classes = new ArrayList<>();
    if (!directory.exists()) {
      return classes;
    }
    File[] files = directory.listFiles();
    if (null == files) {
      return classes;
    }
    for (File file : files) {
      if (file.isDirectory()) {
        assert !file.getName().contains(".");
        classes.addAll(findClasses(file, packageName + "." + file.getName()));
      } else if (file.getName().endsWith(".class")) {
        try {
          classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
        } catch (ClassNotFoundException e) {
          throw new ClassLoadingException(e);
        }
      }
    }
    return classes;
  }

  public static boolean isSimpleValueType(Class<?> clazz) {
    return isPrimitiveOrWrapper(clazz) || clazz.isEnum() ||
        CharSequence.class.isAssignableFrom(clazz) ||
        Number.class.isAssignableFrom(clazz) ||
        Date.class.isAssignableFrom(clazz) ||
        URI.class == clazz || URL.class == clazz ||
        Locale.class == clazz || Class.class == clazz;
  }

  public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
    return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
  }

  public static boolean isPrimitiveWrapper(Class<?> clazz) {
    return primitiveWrapperTypes.contains(clazz);
  }
}
