package top.yeonon.lmserver.utils;

/**
 * @Author yeonon
 * @date 2018/11/25 0025 15:28
 **/
public final class JDKVersionUtil {

    private static final String javaVersion;

    private static boolean isJava8;

    static {
        javaVersion = System.getProperty("java.version");
        if (javaVersion.contains("1.8")) {
            isJava8 = true;
        }
    }

    public static String getJavaVersion() {
        return javaVersion;
    }

    public static boolean isJava8() {
        return isJava8;
    }
}
