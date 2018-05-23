package top.yeonon.lmserver.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @Author yeonon
 * @date 2018/5/23 0023 14:02
 **/
public final class PropertiesUtil {

    private static final Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

    private static final Properties props;

    static {
        String fileName = "application.properties";
        props = new Properties();
        try {
            InputStream stream = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
            if (stream != null)
                props.load(new InputStreamReader(stream,"UTF-8"));
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * 获取String类型的值
     * @param key 键
     * @return String类型的值（可能为null）
     */
    public static String getStringProperty(String key) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value))
            return null;
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
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Integer.valueOf(value.trim());
    }


    /**
     * 获取Integer类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return Integer类型的值（不为null）
     */
    public static Integer getIntegerProperty(String key, Integer defaultValue) {
        Integer value = PropertiesUtil.getIntegerProperty(key);

        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 返回Boolean类型的属性
     * @param key 键
     * @return Boolean类型的值或者null
     */
    public static Boolean getBooleanProperty(String key) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Boolean.valueOf(value.trim());
    }

    /**
     * 返回Boolean类型的属性
     * @param key 键
     * @param defaultValue 默认值
     * @return Boolean类型的值
     */
    public static Boolean getBooleanProperty(String key, Boolean defaultValue) {
        Boolean value = PropertiesUtil.getBooleanProperty(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

}
