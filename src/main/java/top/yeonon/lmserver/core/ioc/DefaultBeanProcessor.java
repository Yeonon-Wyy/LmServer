package top.yeonon.lmserver.core.ioc;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import top.yeonon.lmserver.annotation.*;
import top.yeonon.lmserver.controller.LmHttpHandler;
import top.yeonon.lmserver.filter.LmFilter;
import top.yeonon.lmserver.interceptor.LmInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author yeonon
 * @date 2018/5/31 0031 19:37
 **/
public class DefaultBeanProcessor extends AbstractBeanProcessor{

    private static final Logger log = Logger.getLogger(DefaultBeanProcessor.class);


    //http handler maps
    private static final Map<String, LmHttpHandler> httpHandlerMaps = new HashMap<>();

    //filter maps
    private static final Map<String, List<LmFilter> > filterMaps = new HashMap<>();

    //interceptor maps
    private static final Map<String, List<LmInterceptor>> interceptorMaps = new HashMap<>();

    private static Map<String, Class<?>> typeMaps = null;

    private final String packageName;

    public DefaultBeanProcessor(String packageName) {
        this.packageName = packageName;
        typeMaps = super.getType();
    }

    //核心方法
    /**
     * 处理容器中的所有Bean，主要是分类，依赖注入等
     */
    @Override
    public void processBean(boolean isMultiThread) {
        //先去处理Bean
        super.beanProcessor(packageName, isMultiThread);

        super.getBeanMaps().forEach((clz, beanInstance) -> {
            if (LmInterceptor.class.isAssignableFrom(clz) &&
                    clz.isAnnotationPresent(Interceptor.class)) {
                //如果是拦截器，则执行拦截器的处理逻辑
                processInterceptor(clz, beanInstance);
            }
            else if (LmFilter.class.isAssignableFrom(clz) &&
                    clz.isAnnotationPresent(Filter.class)) {
                //如果是Filter，则执行对Filter的处理逻辑
                processFilter(clz, beanInstance);
            } else if (clz.isAnnotationPresent(Controller.class)) {
                //如果是controller,则执行对controller的处理逻辑
                processController(clz, beanInstance);
            }

            processBeanWire(clz, beanInstance);
        });

        //处理filter顺序
        filterMaps.forEach((url, filters) -> {
            Collections.sort(filters);
        });

        //处理拦截器顺序
        interceptorMaps.forEach((url, interceptors) -> {
            Collections.sort(interceptors);
        });
    }



    /**
     * 实现依赖注入
     * @param clz 类
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

                                if (super.getBeanMaps().get(fieldClass) != null) {
                                    //如果容器中存在这个字段的类，则将其赋值
                                    field.set(beanInstance, super.getBeanMaps().get(fieldClass));
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
     * 对Controller处理
     * @param clz 类
     * @param beanInstance 类实例
     */
    private void processController(Class<?> clz, Object beanInstance) {
        log.info("加载controller ： " + clz.getName());
        //实例化该类
        //从class对象中得到该类声明的方法集合
        Method[] methods = clz.getDeclaredMethods();
        //遍历方法集合
        for (Method method : methods) {
            //如果包含RequestMapping注解，就是我们要的
            if (method != null && method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = method.
                        getAnnotation(RequestMapping.class);
                //拿到注解上的值（Url集合）
                String[] urls = requestMapping.value();
                //遍历url集合
                for (String url : urls) {
                    //如果httpHandlerMaps没有这个url，就往里面添加 url -> httpHandler映射
                    if (httpHandlerMaps.get(url) == null) {
                        log.info("加载requestMapping : url is " + url);
                        //构造映射关系
                        httpHandlerMaps.put(url, new LmHttpHandler(beanInstance, method));
                    } else {
                        log.info("已经做过该Url的映射");
                    }
                }
            }
        }
    }


    /**
     * 处理Interceptor
     * @param clz 类
     * @param beanInstance 类实例
     */
    private void processInterceptor(Class<?> clz, Object beanInstance) {
        LmInterceptor interceptorInstance = (LmInterceptor) beanInstance;
        Interceptor interceptor = clz.getAnnotation(Interceptor.class);
        String[] urls = interceptor.value();
        for (String url : urls) {
            if (interceptorMaps.get(url) == null) {
                log.info("加载interface " + clz.getName() + "url 是" + url);
                interceptorMaps.put(url, Lists.newArrayList(interceptorInstance));
            } else {
                interceptorMaps.get(url).add(interceptorInstance);
            }
        }
    }

    /**
     * 处理Filter
     * @param clz 类
     * @param beanInstance 类实例
     */
    private void processFilter(Class<?> clz, Object beanInstance) {
        LmFilter filterInstance = (LmFilter) beanInstance;
        Filter filter = clz.getAnnotation(Filter.class);
        String[] urls = filter.value();
        for (String url : urls) {
            if (filterMaps.get(url) == null) {
                log.info("加载filter " + clz.getName() + " 要过滤的url是 ： " + url);
                filterMaps.put(url, Lists.newArrayList(filterInstance));
            } else {
                filterMaps.get(url).add(filterInstance);
            }
        }
    }



    public static LmHttpHandler getHandler(String url) {
        return httpHandlerMaps.get(url);
    }

    public static List<LmFilter> getFilter(String url) {
        return filterMaps.get(url);
    }

    public static List<LmInterceptor> getInterceptor(String url) {
        return interceptorMaps.get(url);
    }

    public static Class<?> getClassType(String typename) {
        return typeMaps.get(typename);
    }
}
