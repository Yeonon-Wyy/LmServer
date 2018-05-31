package top.yeonon.lmserver.core.ioc.autowire;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yeonon.lmserver.annotation.Autowire;
import top.yeonon.lmserver.annotation.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/5/31 0031 21:02
 **/
public class BeanWire {

    private static final Logger log = LoggerFactory.getLogger(BeanWire.class);

    public static void doWire(Map<Class<?>, Object> beanMaps) {
        try {
            for (Map.Entry<Class<?>, Object> entry : beanMaps.entrySet()) {
                Class<?> clz = entry.getKey();
                if (clz.getAnnotations() != null) {
                    Annotation[] annotations = clz.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().isAnnotationPresent(Component.class)) {
                            Field[] fields = clz.getDeclaredFields();
                            for (Field field : fields) {
                                if (field.isAnnotationPresent(Autowire.class)) {
                                    field.setAccessible(true);
                                    Class<?> fieldClass = field.getType();

                                    if (beanMaps.get(fieldClass) != null) {
                                        field.set(entry.getValue(), beanMaps.get(fieldClass));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            log.error(e.toString());
        }

    }
}
