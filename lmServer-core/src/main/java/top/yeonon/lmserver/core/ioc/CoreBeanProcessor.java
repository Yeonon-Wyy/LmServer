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
import java.util.Arrays;

/**
 * 核心Bean处理器
 *
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
     *
     * @param clz      属性配置类
     * @param instance 实例
     */
    private void processProperties(Class<?> clz, Object instance) {
        if (!clz.isAnnotationPresent(PropertiesConfiguration.class)) return;

        //获取属性值前缀
        String prefix = clz.getAnnotation(PropertiesConfiguration.class).prefix();

        Arrays.stream(clz.getDeclaredFields()).forEach(field ->
                processFieldOfProperties(prefix, field, instance)
        );
        log.info("load properties : " + clz.getName());

    }


    /**
     * 处理属性处理器中的字段
     *
     * @param prefix
     * @param field
     * @param instance
     */
    private void processFieldOfProperties(String prefix, Field field, Object instance) {
        field.setAccessible(true);
        String key = prefix + "." + field.getName();
        try {
            field.set(instance, PropertiesUtil.getProperty(field.getType(), key));
        } catch (IllegalAccessException e) {
            log.error("parse the properties failed!");
        }
    }

    /**
     * 实现依赖注入
     *
     * @param clz          类
     * @param beanInstance 类实例
     */
    private void processBeanWire(Class<?> clz, Object beanInstance) {
        if (clz.getAnnotations() == null) return;
        Annotation[] annotations = clz.getAnnotations();
        Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(Bean.class))
                .flatMap(annotation -> Arrays.stream(clz.getDeclaredFields()))
                .filter(field -> field.isAnnotationPresent(Autowire.class))
                .forEach(field -> processFieldOfBeanWire(field, beanInstance));

    }

    /**
     * 依赖注入时，处理字段的方法
     *
     * @param field
     * @param beanInstance
     */
    private void processFieldOfBeanWire(Field field, Object beanInstance) {
        field.setAccessible(true);
        Class<?> fieldClass = field.getType();

        if (getBeanMaps().get(fieldClass) != null) {
            //如果容器中存在这个字段的类，则将其赋值
            try {
                field.set(beanInstance, getBeanMaps().get(fieldClass));
            } catch (IllegalAccessException e) {
                log.error(e.getCause().toString());
            }
        }
    }


    /**
     * 处理配置类
     * <p>
     * 在该方法里会提前处理配置类的依赖注入项，之所以有效，是因为基于这样一个前提：
     * 在处理配置类之前，已经把整个系统中除了配置类里配置的所有Bean都实例化了。
     * </p>
     * 在这个前提下，如果用户在配置类中对配置类中的配置项做依赖注入，本身就是不合理的，理应抛出异常。举个例子：
     * <p>
     * class MyConfig {
     * </p>
     * //这里是@Autowrie注解的字段，其注入的类型是A（这显然是不合理的，即使有多个配置类，也并不合理）
     * <p>
     * //这里是@Bean注解的方法，用于配置Bean，设这个Bean名字为A
     * }
     *
     * @param clz
     */
    private void processConfigBean(Class<?> clz, Object instance) {
        if (!clz.isAnnotationPresent(Configuration.class)) return;

        //处理配置类的依赖注入
        Arrays.stream(clz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Autowire.class))
                .forEach(field -> processFieldOfConfigBean(field, instance));

        //加载有Bean注解的方法（暂时仅支持注解在方法上的Bean）
        Arrays.stream(clz.getDeclaredMethods())
                .filter(method -> method.getAnnotation(Bean.class) != null)
                .forEach(method -> processMethodOfConfig(method, instance));
    }

    /**
     * 处理配置类时，对字段的处理
     *
     * @param field
     * @param instance
     */
    private void processFieldOfConfigBean(Field field, Object instance) {
        Object fieldInstance = super.getBeanMaps().get(field.getType());
        field.setAccessible(true);
        try {
            field.set(instance, fieldInstance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理配置类时，对方法的处理
     *
     * @param method
     * @param instance
     */
    private void processMethodOfConfig(Method method, Object instance) {
        //这里要调用这个方法，因为用户会对方法做一些配置
        try {
            Object returnInstance = method.invoke(instance);
            Class<?> returnType = returnInstance.getClass();
            super.getBeanMaps().putIfAbsent(returnType, returnInstance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage());
        }
    }
}
