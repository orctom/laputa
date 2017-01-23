package com.orctom.laputa.utils;

import com.orctom.laputa.exception.ClassLoadingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Class Utils
 * Created by hao-chen2 on 1/5/2015.
 */
public class ClassUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtils.class);

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
    return getClasses(packageName)
        .stream()
        .filter(clazz -> clazz.isAnnotationPresent(annotationClass))
        .collect(Collectors.toList());
  }

  /**
   * Scans all classes accessible from the context class loader which belong
   * to the given package and subpackages.
   *
   * @param packageName The base package
   * @return The classes
   * @throws ClassLoadingException
   */
  public static List<Class<?>> getClasses(String packageName) {
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      String packagePath = packageName.replace('.', '/');
      Enumeration<URL> resources = classLoader.getResources(packagePath);
      List<Class<?>> classes = new ArrayList<>();
      while (resources.hasMoreElements()) {
        URL resource = resources.nextElement();
        String protocol = resource.getProtocol();
        String path = resource.getPath();
        if ("jar".equals(protocol)) {
          int startIndex = path.startsWith("file:") ? "file:".length() : 0;
          int endIndex = path.endsWith("jar") ? 0 : path.indexOf("!");
          File jarFilePath = new File(path.substring(startIndex, endIndex));
          classes.addAll(findClassesInJar(jarFilePath, packagePath));
        } else if ("file".equals(protocol)) {
          classes.addAll(findClassesInFile(new File(path), packageName));
        } else {
          throw new UnsupportedOperationException("Not supported reading from: " + protocol);
        }
      }

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Found {} classes:", classes.size());
        classes.forEach(clazz -> LOGGER.trace(clazz.getName()));
      }
      return classes;
    } catch (IOException e) {
      throw new ClassLoadingException(e);
    }
  }

  private static List<Class<?>> findClassesInJar(File jarFilePath, String packagePath) {
    try {
      JarFile jarFile = new JarFile(jarFilePath.getAbsoluteFile());
      return jarFile.stream()
          .filter(entry -> isClassInPackage(entry, packagePath))
          .map(entry -> loadClass(entry.getName().replaceAll("/", ".")))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new ClassLoadingException(e);
    }
  }

  private static boolean isClassInPackage(JarEntry entry, String packagePath) {
    return !entry.isDirectory() &&
        entry.getName().endsWith(".class") &&
        entry.getName().startsWith(packagePath);
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
  private static List<Class<?>> findClassesInFile(File directory, String packageName) {
    File[] files = directory.listFiles();
    if (null == files || 0 == files.length) {
      return Collections.emptyList();
    }

    List<Class<?>> classes = new ArrayList<>();
    for (File file : files) {
      if (file.isDirectory()) {
        assert !file.getName().contains(".");
        classes.addAll(findClassesInFile(file, packageName + "." + file.getName()));

      } else if (file.getName().endsWith(".class")) {
        classes.add(loadClass(packageName + '.' + file.getName()));
      }
    }
    return classes;
  }

  private static Class<?> loadClass(String classFileName) {
    try {
      String className = classFileName.substring(0, classFileName.length() - 6);
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new ClassLoadingException(e);
    }
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
