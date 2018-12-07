package top.yeonon.lmserver.core.ioc;

import org.apache.log4j.Logger;
import top.yeonon.lmserver.core.annotation.Autowire;
import top.yeonon.lmserver.core.annotation.Bean;
import top.yeonon.lmserver.core.annotation.Configuration;
import top.yeonon.lmserver.core.annotation.PropertiesConfiguration;
import top.yeonon.lmserver.core.utils.ClassUtil;
import top.yeonon.lmserver.core.utils.PropertiesUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BeanProcessor抽象基类，包含共用的方法
 *
 * @Author yeonon
 * @date 2018/5/29 0029 21:27
 **/
public abstract class AbstractBeanProcessor implements BeanProcessor {

    private static final Logger log = Logger.getLogger(AbstractBeanProcessor.class);

    //bean maps
    private static final Map<Class<?>, Object> beanMaps = new ConcurrentHashMap<>();

    //class map
    private static final Map<String, Class<?>> classMaps = new HashMap<>();

    /**
     * 处理所有的Bean，该方法只会被调用一次，也就是说只会扫描一次
     *
     * @param packageName 包名
     */
    @Override
    public void beanProcessor(String packageName, boolean isMultiThread) {
        //获取该包下的所有类
        Set<Class<?>> classSets = ClassUtil.getClassFromPackage(packageName, isMultiThread);

        try {
            for (Class<?> clz : classSets) {
                if (clz != null) {
                    classMaps.put(clz.getTypeName(), clz);
                }
                //如果该类上没有注解，或者clz为null（有可能）就表明不需要往下执行逻辑
                if (clz != null && clz.getAnnotations().length != 0) {
                    //如果是普通的组件，那么就做对应的处理
                    Annotation[] annotations = clz.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().isAnnotationPresent(Bean.class)) {
                            beanMaps.put(clz, clz.newInstance());
                        }
                    }

                }
            }


        } catch (IllegalAccessException | InstantiationException e) {
            log.error(e.toString());
        }
    }

    /**
     * 子类处理Bean的具体逻辑
     */
    protected abstract void processBean();


    public Map<Class<?>, Object> getBeanMaps() {
        return beanMaps;
    }

    public Map<String, Class<?>> getType() {
        return classMaps;
    }
}
