package com.orctom.laputa.utils;

import com.orctom.laputa.exception.ClassLoadingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class Utils
 * Created by hao-chen2 on 1/5/2015.
 */
public class ClassUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtils.class);

  private static final Set<Class<?>> primitiveWrapperTypes = new HashSet<>(8);
  private static final String CHARSET = Charset.defaultCharset().name();

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

  public static void getClassesWithAnnotation(String packageName,
                                              Class<? extends Annotation> annotationClass,
                                              ClassVisitor visitor)
      throws ClassLoadingException {
    LOGGER.trace("Finding classes with annotation: {}, in package: {}", annotationClass, packageName);
    try {
      ClassLoader classLoader = getClassLoader();
      String packagePath = packageName.replace('.', '/');
      Enumeration<URL> resources = classLoader.getResources(packagePath);
      while (resources.hasMoreElements()) {
        URL resource = resources.nextElement();
        String protocol = resource.getProtocol();
        String path = URLDecoder.decode(resource.getPath(), CHARSET);
        if ("jar".equals(protocol)) {
          int startIndex = path.startsWith("file:") ? "file:".length() : 0;
          int endIndex = path.endsWith("jar") ? 0 : path.indexOf("!");
          File jarFilePath = new File(path.substring(startIndex, endIndex));
          findClassesInJar(jarFilePath, packagePath, annotationClass, visitor);
        } else if ("file".equals(protocol)) {
          findClassesInFile(new File(path), packageName, annotationClass, visitor);
        } else {
          throw new UnsupportedOperationException("Not supported reading from: " + protocol);
        }
      }
    } catch (IOException e) {
      throw new ClassLoadingException(e);
    }
  }

  private static ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

  private static void findClassesInJar(File jarFilePath,
                                                 String packagePath,
                                                 Class<? extends Annotation> annotationClass,
                                                 ClassVisitor visitor) {
    LOGGER.trace("Finding in jar: {}" + jarFilePath.getAbsolutePath());
    try {
      JarFile jarFile = new JarFile(jarFilePath.getAbsoluteFile());
      jarFile.stream()
          .filter(entry -> isClassInPackage(entry, packagePath))
          .map(entry -> loadClass(entry.getName(), packagePath))
          .filter(clazz -> clazz.isAnnotationPresent(annotationClass))
          .forEach(visitor::visit);
    } catch (IOException e) {
      throw new ClassLoadingException(e);
    }
  }

  private static boolean isClassInPackage(JarEntry entry,  String packagePath) {
    String name = entry.getName();
    boolean isQualified = !entry.isDirectory() &&
        name.endsWith(".class") &&
        name.contains(packagePath);
    LOGGER.trace("{}: {}", name, isQualified);
    return isQualified;
  }

  private static void findClassesInFile(File directory,
                                        String packageName,
                                        Class<? extends Annotation> annotationClass,
                                        ClassVisitor visitor) {
    LOGGER.trace("Finding in directory: {}", directory.getAbsolutePath());

    File[] files = directory.listFiles();
    if (null == files || 0 == files.length) {
      LOGGER.trace("Finding nothing.");
      return;
    }

    for (File file : files) {
      if (file.isDirectory()) {
        assert !file.getName().contains(".");
        findClassesInFile(file, packageName + "." + file.getName(), annotationClass, visitor);

      } else if (file.getName().endsWith(".class")) {
        Class<?> clazz = loadClass(packageName + '.' + file.getName());
        if (clazz.isAnnotationPresent(annotationClass)) {
          visitor.visit(clazz);
        }
      }
    }
  }

  private static Class<?> loadClass(String classFileName, String packagePath) {
    int beginIndex = classFileName.indexOf(packagePath);
    int endIndex = classFileName.length() - 6;
    try {
      String className = classFileName.substring(beginIndex, endIndex).replaceAll("/", ".");
      return Class.forName(className, false, getClassLoader());
    } catch (ClassNotFoundException e) {
      throw new ClassLoadingException(e);
    }
  }

  private static Class<?> loadClass(String classFileName) {
    try {
      String className = classFileName.substring(0, classFileName.length() - 6);
      return Class.forName(className, false, getClassLoader());
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
