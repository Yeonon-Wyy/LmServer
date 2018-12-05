package top.yeonon.lmserver.core.utils;

import org.apache.log4j.Logger;
import top.yeonon.lmserver.core.ioc.AbstractBeanProcessor;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 遍历包名下的所有类
 * @Author yeonon
 * @date 2018/5/22 0022 21:57
 **/
public final class ClassUtil {

    private static final Logger log = Logger.getLogger(AbstractBeanProcessor.class);


    /**
     *
     * @param packageName 包名
     * @param isMultiThread 是否需要开启多线程加速
     * @return 类集合
     */
    public static Set<Class<?>> getClassFromPackage(String packageName, boolean isMultiThread) {
        Set<Class<?>> classSet = new HashSet<>();
        String packageDirName = packageName.replace('.', '/');

        try {
            //从类路径开始，根据包名获取url集合
            Enumeration<URL> urls = ClassUtil.class.getClassLoader().getResources(packageDirName);
            //遍历每一个元素
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                //如果是普通文件的形式
                if ("file".equalsIgnoreCase(protocol)) {
                    //将Url转换成文件系统的路径表示
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    if (isMultiThread) {
                        //如果用户选择使用多线程，那么就使用ForkJoin的方式
                        findClassesByForkJoin(packageName, filePath, classSet);
                    } else {
                        //否则使用普通的单线程方式
                        findClassesByNormal(packageName, filePath, classSet);
                    }
                } else if ("jar".equalsIgnoreCase(protocol)) {
                    //如果是jar包的形式，那么就做对应处理(一般当框架打包之后作为第三方库使用的话，都会是Jar包的形式)
                    findClassOnJar(packageDirName, url, classSet);
                }
            }
        } catch (IOException e) {
            log.error(e.toString());
        }

        return classSet;
    }

    /**
     * 在Jar包中查找类
     * @param packageDirname 包名称（不是包全限定类名，而是类似 xxx/xxx/xxx这样的路径）
     * @param url 即Jar包的URL
     * @param classSet 类对象集合
     * @throws IOException
     */
    private static void findClassOnJar(String packageDirname, URL url, Set<Class<?>> classSet) throws IOException {
        JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
        Enumeration<JarEntry> entries  = jar.entries();
        String packageName;
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if (name.startsWith(packageDirname)) {
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    packageName = name.substring(0, idx).replace('/','.');
                    if (name.endsWith(".class") && !entry.isDirectory()) {
                        // 去掉后面的".class" 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        try{
                            // 添加到classes
                            classSet.add(Class.forName(packageName + '.' + className));
                        }catch (ClassNotFoundException e){
                            log.error("can't found the class : " + className);
                        }
                    }
                }

            }
        }
    }


    /**
     * 使用单线程递归的方式查找类
     *
     * @param packageName 宝名
     * @param filePath    路径
     * @param classSet    类集合
     */
    private static void findClassesByNormal(String packageName, String filePath, Set<Class<?>> classSet) {
        File dir = new File(filePath);

        //如果该文件不存在或者不是目录，那么直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        //列出当前目录的所有符合要求的项
        File[] dirFiles = dir.listFiles(pathName -> pathName.isDirectory() || pathName.getName().endsWith(".class"));

        if (dirFiles == null || dirFiles.length == 0) {
            return;
        }


        String className;
        Class clz;

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                //如果是目录，就递归进入目录
                findClassesByNormal(packageName + "." + file.getName(), filePath + "/" + file.getName(), classSet);
                continue;
            }

            //否则直接加载类文件
            className = file.getName().substring(0, file.getName().length() - 6);
            clz = loadClass(packageName + "." + className);

            classSet.add(clz);
        }

    }


    /**
     * 使用Fork-Join框架加速查找类
     *
     * @param packageName 包名
     * @param filePath    文件路径
     * @param classSet    类集合
     */
    private static void findClassesByForkJoin(String packageName, String filePath, Set<Class<?>> classSet) {
        //创建一个任务
        FindFileTask task = new FindFileTask(new File(filePath), packageName);

        ForkJoinPool pool = new ForkJoinPool();
        //提交一个任务
        Future<Set<Class<?>>> future = pool.submit(task);

        try {
            //将结果加入到classSet总集合中
            classSet.addAll(future.get());
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.toString());
        }
    }


    /**
     * ForkJoin框架的任务
     */
    private static class FindFileTask extends RecursiveTask<Set<Class<?>>> {

        //文件
        private File file;
        //包名
        private String packageName;

        public FindFileTask(File file, String packageName) {
            this.file = file;
            this.packageName = packageName;
        }


        @Override
        protected Set<Class<?>> compute() {
            Set<Class<?>> classSet = new HashSet<>();

            File[] files = file.listFiles(pathName -> pathName.isDirectory() || pathName.getName().endsWith(".class"));

            if (files == null) {
                return classSet;
            }

            String className;
            Class<?> clz;
            for (File file : files) {
                if (file.isDirectory()) {
                    //如果是目录，就创建子任去执行。
                    FindFileTask task = new FindFileTask(file, packageName + "." + file.getName());
                    task.fork(); //创建子任务
                    //将结果加入到classSet集合里（在这里就是将集合加入到总集合中）
                    classSet.addAll(task.join());
                } else {
                    //加载类
                    className = file.getName().substring(0, file.getName().length() - 6);
                    clz = loadClass(packageName + "." + className);
                    //将加载好的类加入到集合
                    classSet.add(clz);
                }
            }
            return classSet;
        }
    }


    /**
     * 加载类
     *
     * @param className 类名
     * @return 类变量
     */
    private static Class<?> loadClass(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
