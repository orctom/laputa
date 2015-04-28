package com.orctom.laputa.util;

import com.orctom.laputa.util.exception.ClassLoadingException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by hao-chen2 on 1/5/2015.
 */
public class ClassUtils {

    /**
     * @param packageName     The base package
     * @param annotationClass The annotation class
     * @return The classes
     * @throws ClassLoadingException
     */
    public static List<Class<?>> getClassesWithAnnotation(String packageName, Class<? extends Annotation> annotationClass)
            throws ClassLoadingException {
        List<Class<?>> classes = getClasses(packageName);
        List<Class<?>> clazzes = new ArrayList<Class<?>>();
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
        List<Class<?>> classes = null;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            assert classLoader != null;
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<File>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String fileName = resource.getFile();
                String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
                dirs.add(new File(fileNameDecoded));
            }
            classes = new ArrayList<Class<?>>();
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
     * @param packageName The package name for classes found inside the base
     *                    directory
     * @return The classes
     * @throws ClassLoadingException
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassLoadingException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
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
}
