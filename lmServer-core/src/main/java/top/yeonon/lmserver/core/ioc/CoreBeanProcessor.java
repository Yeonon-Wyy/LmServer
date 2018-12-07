package top.yeonon.lmserver.core.ioc;

import org.apache.log4j.Logger;
import top.yeonon.lmserver.core.annotation.Autowire;
import top.yeonon.lmserver.core.annotation.Bean;
import top.yeonon.lmserver.core.annotation.Configuration;
import top.yeonon.lmserver.core.annotation.PropertiesConfiguration;
import top.yeonon.lmserver.core.utils.PropertiesUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * 核心Bean处理器
 * @Author yeonon
 * @date 2018/12/7 0007 12:54
 **/
public class CoreBeanProcessor extends AbstractBeanProcessor {


    private static final Logger log = Logger.getLogger(CoreBeanProcessor.class);

    private final String packageName;
    private final boolean isMultiThread;

    public CoreBeanProcessor(String packageName, boolean isMultiThread) {
        this.packageName = packageName;
        this.isMultiThread = isMultiThread;
    }

    /**
     * TODO 现在还无法解决如何在@Configuration配置类里注入Bean
     */
    @Override
    public void processBean() {
        //先去处理Bean
        super.beanProcessor(packageName, isMultiThread);

        //处理属性配置类
        super.getBeanMaps().forEach(this::processProperties);

        //处理配置类
        super.getBeanMaps().forEach(this::processConfigBean);

        //处理依赖注入
        super.getBeanMaps().forEach(this::processBeanWire);
    }


    /**
     * 处理属性配置类
     * @param clz 属性配置类
     * @param instance 实例
     */
    private void processProperties(Class<?> clz, Object instance) {
        if (clz.isAnnotationPresent(PropertiesConfiguration.class)) {
            String prefix = clz.getAnnotation(PropertiesConfiguration.class).prefix();
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String key = prefix + "." + field.getName();
                try {
                    field.set(instance, PropertiesUtil.getProperty(field.getType(), key));
                } catch (IllegalAccessException e) {
                    log.error("parse the properties failed!");
                }
            }
            log.info("load properties : " + clz.getName());
        }
    }

    /**
     * 实现依赖注入
     *
     * @param clz          类
     * @param beanInstance 类实例
     */
    private void processBeanWire(Class<?> clz, Object beanInstance) {
        try {
            if (clz.getAnnotations() != null) {
                Annotation[] annotations = clz.getAnnotations();
                for (Annotation annotation : annotations) {
                    //判断这个类上的注解是否也是Component(注解上可以有注解)
                    if (annotation.annotationType().isAnnotationPresent(Bean.class)) {
                        Field[] fields = clz.getDeclaredFields();
                        //获取所有字段
                        for (Field field : fields) {
                            if (field.isAnnotationPresent(Autowire.class)) {
                                field.setAccessible(true);
                                Class<?> fieldClass = field.getType();

                                if (getBeanMaps().get(fieldClass) != null) {
                                    //如果容器中存在这个字段的类，则将其赋值
                                    field.set(beanInstance, getBeanMaps().get(fieldClass));
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            log.error(e.getCause().toString());
        }
    }

    /**
     *
     * 处理配置类
     *
     * 在该方法里会提前处理配置类的依赖注入项，之所以有效，是因为基于这样一个前提：
     * 在处理配置类之前，已经把整个系统中除了配置类里配置的所有Bean都实例化了。
     *
     * 在这个前提下，如果用户在配置类中对配置类中的配置项做依赖注入，本身就是不合理的，理应抛出异常。举个例子：
     *
     * class MyConfig {
     *
     *      //这里是@Autowrie注解的字段，其注入的类型是A（这显然是不合理的，即使有多个配置类，也并不合理）
     *
     *      //这里是@Bean注解的方法，用于配置Bean，设这个Bean名字为A
     * }
     *
     *
     *
     * @param clz
     */
    private void processConfigBean(Class<?> clz, Object instance) {
        if (!clz.isAnnotationPresent(Configuration.class)) return;
        try {
            //处理配置类的依赖注入
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowire.class)) {
                    Object fieldInstance = super.getBeanMaps().get(field.getType());
                    field.setAccessible(true);
                    field.set(instance, fieldInstance);
                }
            }

            //加载有Bean注解的方法（暂时仅支持注解在方法上的Bean）
            Method[] methods = clz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getAnnotation(Bean.class) == null) continue;
                Object returnInstance;
                //这里要调用这个方法，因为用户会对方法做一些配置
                returnInstance = method.invoke(instance);
                Class<?> returnType = returnInstance.getClass();
                if (super.getBeanMaps().get(returnType) == null) {
                    super.getBeanMaps().put(returnType, returnInstance);
                    log.info("load Bean ： " + returnType);
                } else {
                    log.info("this Bean has been loaded : " + returnType);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
