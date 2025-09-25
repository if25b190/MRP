package me.duong.mrp.utils;

import me.duong.mrp.Logger;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            String path = packageName.replace('.', '/');
            List<Class<?>> classes = new ArrayList<>();
            var packageUrl = classLoader.getResource(path);
            if (packageUrl != null && "jar".equals(packageUrl.getProtocol())) {
                String jarFileName = URLDecoder.decode(packageUrl.getFile(), StandardCharsets.UTF_8);
                jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));
                try (JarFile jf = new JarFile(jarFileName)) {
                    Enumeration<JarEntry> jarEntries = jf.entries();
                    while (jarEntries.hasMoreElements()) {
                        String entryName = jarEntries.nextElement().getName();
                        if (entryName.startsWith(path) && entryName.length() > path.length() + 5) {
                            entryName = entryName
                                    .substring(path.length(), entryName.lastIndexOf('.'))
                                    .replaceAll("/", "");
                            var clazz = getClass(entryName, packageName);
                            if (clazz != null) {
                                classes.add(clazz);
                            }
                        }
                    }
                }
            } else {
                try (InputStream in = classLoader.getResourceAsStream(path)) {
                    if (in != null) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                            reader.lines()
                                    .filter(line -> line.endsWith(".class"))
                                    .map(line -> getClass(line, packageName))
                                    .forEach(classes::add);
                        }
                    }
                }
            }
            return classes;
        } catch (IOException exception) {
            Logger.error("Failed to ", exception.getMessage());
        }
        return List.of();
    }

    /**
     * Finds the class within the specified package
     *
     * @param className   The class name to find
     * @param packageName The package name where the class resides
     * @return the class object
     */
    private static Class<?> getClass(String className, String packageName) {
        try {
            int idx = className.lastIndexOf('.');
            return Class.forName(packageName + "."
                    + (idx != -1 ? className.substring(0, idx) : className));
        } catch (ClassNotFoundException e) {
            Logger.error("Class not found: %s\n%s", className, e.getMessage());
        }
        return null;
    }
}
