package top.yeonon.lmserver.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
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
            case "STRING": return getStringProperty(key);
            case "INTEGER": return getIntegerProperty(key);
            case "INT": return getIntegerProperty(key);
            case "SHORT": return getShortProperty(key);
            case "LONG": return getLongProperty(key);
            case "BYTE": return getByteProperty(key);
            case "FLOAT": return getFloatProperty(key);
            case "DOUBLE": return getDoubleProperty(key);
            case "BOOLEAN": return getBooleanProperty(key);
            default:return null;
        }
    }

    /**
     * 获取String类型的值
     * @param key 键
     * @return String类型的值（可能为null）
     */
    public static String getStringProperty(String key) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value;
    }

    /**
     * 获取String类型的值（可提供默认值）
     * @param key 键
     * @param defaultValue 默认值
     * @return String类型的值（不为null）
     */
    public static String getStringProperty(String key, String defaultValue) {
        String value = PropertiesUtil.getStringProperty(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }


    /**
     * 获取Integer类型的属性
     * @param key 键
     * @return Integer类型的值或者null
     */
    public static Integer getIntegerProperty(String key) {
        String value = getStringProperty(key);
        return value == null ? null : Integer.valueOf(value);
    }


    /**
     * 获取Integer类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return Integer类型的值（不为null）
     */
    public static Integer getIntegerProperty(String key, Integer defaultValue) {
        Integer value = getIntegerProperty(key);
        return value == null ? defaultValue : value;
    }

    /**
     * 返回Long类型的属性
     * @param key 键值
     * @return
     */
    public static Long getLongProperty(String key) {
        String value = getStringProperty(key);
        return value == null ? null : Long.parseLong(value);
    }

    /**
     * 返回Long类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    public static Long getLongProperty(String key, Long defaultValue) {
        Long value = getLongProperty(key);
        return value == null ? defaultValue : value;
    }

    /**
     * 返回Short类型的属性
     * @param key 键
     * @return
     */
    public static Short getShortProperty(String key) {
        String value = getStringProperty(key);
        return value == null ? null : Short.parseShort(value);
    }

    /**
     * 返回Short类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    public static Short getShortProperty(String key, Short defaultValue) {
        Short value = PropertiesUtil.getShortProperty(key);
        return value == null ? defaultValue : value;
    }

    /**
     * 返回Byte类型的属性
     * @param key 键
     * @return
     */
    public static Byte getByteProperty(String key) {
        String value = getStringProperty(key);
        return value == null ? null : Byte.parseByte(value);
    }

    /**
     * 返回Byte类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    public static Byte getByteProperty(String key, Byte defaultValue) {
        Byte value = getByteProperty(key);
        return value == null ? defaultValue : value;
    }

    /**
     * 返回Float类型的属性
     * @param key 键
     * @return
     */
    public static Float getFloatProperty(String key) {
        String value = getStringProperty(key);
        return value == null ? null : Float.parseFloat(value);
    }

    /**
     * 返回Float类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    public static Float getFloatProperty(String key, Float defaultValue) {
        Float value = getFloatProperty(key);
        return value == null ? defaultValue : value;
    }

    /**
     * 返回Double类型的属性
     * @param key 键
     * @return
     */
    public static Double getDoubleProperty(String key) {
        String value = getStringProperty(key);
        return value == null ? null : Double.parseDouble(value);
    }

    /**
     * 返回Double类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return
     */
    public static Double getDoubleProperty(String key, Double defaultValue) {
        Double value = getDoubleProperty(key);
        return value == null ? defaultValue : value;
    }


    /**
     * 返回Boolean类型的属性
     * @param key 键
     * @return Boolean类型的值或者null
     */
    public static Boolean getBooleanProperty(String key) {
        String value = getStringProperty(key);
        return value == null ? null : Boolean.valueOf(value);
    }

    /**
     * 返回Boolean类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return Boolean类型的值
     */
    public static Boolean getBooleanProperty(String key, Boolean defaultValue) {
        Boolean value = PropertiesUtil.getBooleanProperty(key);
        return value == null ? defaultValue : value;
    }



}
