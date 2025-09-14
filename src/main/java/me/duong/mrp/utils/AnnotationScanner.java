package me.duong.mrp.utils;

import me.duong.mrp.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;

public class AnnotationScanner {
    public static void scanAnnotations(String packageName,
                                        Class<? extends Annotation> annotation,
                                        Consumer<Method> callback) {
        var classes = getClasses(packageName);
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    callback.accept(method);
                }
            }
        }
    }

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     */
    private static List<Class<?>> getClasses(String packageName) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                URI uri = new URI(resource.toString());
                dirs.add(new File(uri.getPath()));
            }
            List<Class<?>> classes = new ArrayList<>();
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
            return classes;
        } catch (ClassNotFoundException | IOException | URISyntaxException exception) {
            Logger.error(exception.getMessage());
        }
        return List.of();
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * sub dirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return List.of();
        }
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
