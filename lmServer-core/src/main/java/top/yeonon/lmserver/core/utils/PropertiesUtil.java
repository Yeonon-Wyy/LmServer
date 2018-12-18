package top.yeonon.lmserver.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Properties;

/**
 * 属性处理工具
 * @Author yeonon
 * @date 2018/5/23 0023 14:02
 **/
public class PropertiesUtil {

    private static final Logger log = Logger.getLogger(PropertiesUtil.class);

    public static final String DEFAULT_PROPERTIES_FILE_NAME = "application.properties";

    private static final Properties props;


    static {
        props = new Properties();
        try {
            InputStream stream = PropertiesUtil.class
                            .getClassLoader()
                            .getResourceAsStream(DEFAULT_PROPERTIES_FILE_NAME);
            if (stream != null) {
                props.load(new InputStreamReader(stream,"UTF-8"));
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }


    /**
     * 根据想要的类型来获取属性值
     * @param returnType 类型
     * @param key 键
     * @return
     */
    public static Object getProperty(Class<?> returnType, String key) {
        String typeName = returnType.getSimpleName().toUpperCase();
        switch (typeName) {
            case "STRING": return getStringProperty(key).orElse(null);
            case "INTEGER": return getIntegerProperty(key).orElse(null);
            case "INT": return getIntegerProperty(key).orElse(null);
            case "SHORT": return getShortProperty(key).orElse(null);
            case "LONG": return getLongProperty(key).orElse(null);
            case "BYTE": return getByteProperty(key).orElse(null);
            case "FLOAT": return getFloatProperty(key).orElse(null);
            case "DOUBLE": return getDoubleProperty(key).orElse(null);
            case "BOOLEAN": return getBooleanProperty(key).orElse(null);
            default:return null;
        }
    }

    /**
     * 获取String类型的值
     * @param key 键
     * @return String类型的值（可能为null）
     */
    public static Optional<String> getStringProperty(String key) {
        Optional<String> value = Optional.ofNullable(props.getProperty(key.trim()));
        return value.filter(StringUtils::isNotBlank);
    }

    /**
     * 获取String类型的值（可提供默认值）
     * @param key 键
     * @param defaultValue 默认值
     * @return String类型的值（不为null）
     */
    public static String getStringProperty(String key, String defaultValue) {
        Optional<String> value = PropertiesUtil.getStringProperty(key);
        return value.orElse(defaultValue);
    }


    /**
     * 获取Integer类型的属性
     * @param key 键
     * @return Integer类型的值或者null
     */
    public static Optional<Integer> getIntegerProperty(String key) {
        Optional<String> value =getStringProperty(key);
        return value.flatMap((v) -> Optional.of(Integer.valueOf(v)));
    }


    /**
     * 获取Integer类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return Integer类型的值（不为null）
     */
    public static Integer getIntegerProperty(String key, Integer defaultValue) {
        Optional<Integer> value = getIntegerProperty(key);
        return value.orElse(defaultValue);
    }

    /**
     * 返回Long类型的属性
     * @param key 键值
     * @return
     */
    public static Optional<Long> getLongProperty(String key) {
        Optional<String> value = getStringProperty(key);
        return value.flatMap((v) -> Optional.of(Long.valueOf(v)));
    }

    /**
     * 返回Long类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    public static Long getLongProperty(String key, Long defaultValue) {
        Optional<Long> value = getLongProperty(key);
        return value.orElse(defaultValue);
    }

    /**
     * 返回Short类型的属性
     * @param key 键
     * @return
     */
    public static Optional<Short> getShortProperty(String key) {
        Optional<String> value = getStringProperty(key);
        return value.flatMap((v) -> Optional.of(Short.valueOf(v)));
    }

    /**
     * 返回Short类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    public static Short getShortProperty(String key, Short defaultValue) {
        Optional<Short> value =PropertiesUtil.getShortProperty(key);
        return value.orElse(defaultValue);
    }

    /**
     * 返回Byte类型的属性
     * @param key 键
     * @return
     */
    public static Optional<Byte> getByteProperty(String key) {
        Optional<String> value = getStringProperty(key);
        return value.flatMap((v) -> Optional.of(Byte.valueOf(v)));
    }

    /**
     * 返回Byte类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    public static Byte getByteProperty(String key, Byte defaultValue) {
        Optional<Byte> value = getByteProperty(key);
        return value.orElse(defaultValue);
    }

    /**
     * 返回Float类型的属性
     * @param key 键
     * @return
     */
    public static Optional<Float> getFloatProperty(String key) {
        Optional<String> value = getStringProperty(key);
        return value.flatMap((v) -> Optional.of(Float.valueOf(v)));
    }

    /**
     * 返回Float类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    public static Float getFloatProperty(String key, Float defaultValue) {
        Optional<Float> value = getFloatProperty(key);
        return value.orElse(defaultValue);
    }

    /**
     * 返回Double类型的属性
     * @param key 键
     * @return
     */
    public static Optional<Double> getDoubleProperty(String key) {
        Optional<String> value = getStringProperty(key);
        return value.flatMap((v) -> Optional.of(Double.valueOf(v)));

    }

    /**
     * 返回Double类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    public static Double getDoubleProperty(String key, Double defaultValue) {
        Optional<Double> value = getDoubleProperty(key);
        return value.orElse(defaultValue);
    }


    /**
     * 返回Boolean类型的属性
     * @param key 键
     * @return Boolean类型的值或者null
     */
    public static Optional<Boolean> getBooleanProperty(String key) {
        Optional<String> value = getStringProperty(key);
        return value.flatMap((v) -> Optional.of(Boolean.valueOf(v)));
    }

    /**
     * 返回Boolean类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return Boolean类型的值
     */
    public static Boolean getBooleanProperty(String key, Boolean defaultValue) {
        Optional<Boolean> value = PropertiesUtil.getBooleanProperty(key);
        return value.orElse(defaultValue);
    }



}
