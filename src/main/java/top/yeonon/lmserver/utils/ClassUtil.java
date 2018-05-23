package top.yeonon.lmserver.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author yeonon
 * @date 2018/5/22 0022 21:57
 **/
public final class ClassUtil {

    public static Set<Class<?>> getClassFromPackage(String packageName) {
        Set<Class<?>> classSet = new HashSet<>();
        String packageDirName = packageName.replace('.', '/');

        try {
            Enumeration<URL> urls =  ClassUtil.class.getClassLoader().getResources(packageDirName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                if ("file".equalsIgnoreCase(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findClassesByFile(packageName, filePath, classSet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classSet;
    }

    private static void findClassesByFile(String packageName, String filePath, Set<Class<?>> classSet) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirFiles = dir.listFiles(pathName -> pathName.isDirectory() || pathName.getName().endsWith(".class"));

        if (dirFiles == null || dirFiles.length == 0) {
            return;
        }


        String className;
        Class clz;

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findClassesByFile(packageName + "." + file.getName(), filePath + "/" + file.getName(), classSet);
                continue;
            }

            className = file.getName().substring(0, file.getName().length() - 6);
            clz = loadClass(packageName + "." + className);

            classSet.add(clz);
        }

    }

    private static Class<?> loadClass(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
