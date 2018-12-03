package top.yeonon.lmserver.core.utils;

/**
 *
 * 检查类文件是否是Java8编译的
 * @Author yeonon
 * @date 2018/11/25 0025 15:28
 **/
public final class JDKVersionUtil {

    private static final String javaVersion;

    private static final String classVersion;

    private static boolean greaterJava8;


    static {
        javaVersion = System.getProperty("java.version");
        classVersion = System.getProperty("java.class.version");
        if (Double.parseDouble(classVersion) >= 52.0) {
            greaterJava8 = true;
        }
    }

    public static String getJavaVersion() {
        return javaVersion;
    }

    public static boolean isGreaterJava8() {
        return greaterJava8;
    }

    public static void main(String[] args) {

    }
}
