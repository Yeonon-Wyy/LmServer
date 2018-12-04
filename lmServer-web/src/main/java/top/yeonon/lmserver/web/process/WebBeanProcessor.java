package top.yeonon.lmserver.web.process;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import top.yeonon.lmserver.core.annotation.Autowire;
import top.yeonon.lmserver.core.annotation.Bean;
import top.yeonon.lmserver.core.annotation.Interceptor;
import top.yeonon.lmserver.core.exception.RequestMethodRepeatException;
import top.yeonon.lmserver.web.annotation.Controller;
import top.yeonon.lmserver.web.annotation.Filter;
import top.yeonon.lmserver.web.filter.LmFilter;
import top.yeonon.lmserver.web.http.LmRequest;
import top.yeonon.lmserver.web.interceptor.LmInterceptor;
import top.yeonon.lmserver.core.ioc.AbstractBeanProcessor;
import top.yeonon.lmserver.core.ioc.Pair;
import top.yeonon.lmserver.web.method.DefaultMethodHandler;
import top.yeonon.lmserver.web.method.MethodHandler;
import top.yeonon.lmserver.web.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/5/31 0031 19:37
 **/
public class WebBeanProcessor extends AbstractBeanProcessor {

    private static final Logger log = Logger.getLogger(WebBeanProcessor.class);


    /**
     * 这里的数据结构有些复杂，稍微解释一下:
     *
     * 首先，这是一个Map，键是String类型的URL，值是一个List列表
     * 然后，List列表的元素是Pair元组类型的，该Pair是由请求方法（GET,POST等）和对应的MethodHandler构成
     * 最后，查找的时候先根据URL查找，拿到对应的List，然后在根据请求方法查找对应的MethodHandler
     *
     * 注：这里用到了Pair元组类型，这是框架中自写的一个类（JDK中没有自带类似功能的组件）。
     */
    private static final Map<String, List<Pair<LmRequest.LMHttpMethod, MethodHandler>>> httpHandlerMaps = new HashMap<>();


    //filter maps
    private static final Map<String, List<LmFilter>> filterMaps = new HashMap<>();

    //interceptor maps
    private static final Map<String, List<LmInterceptor>> interceptorMaps = new HashMap<>();

    private static Map<String, Class<?>> typeMaps = null;

    private final String packageName;

    public WebBeanProcessor(String packageName) {
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

        //拿到beanMap,然后处理各种注解
        super.getBeanMaps().forEach((clz, beanInstance) -> {
            if (LmInterceptor.class.isAssignableFrom(clz) &&
                    clz.isAnnotationPresent(Interceptor.class)) {
                //如果是拦截器，则执行拦截器的处理逻辑
                processInterceptor(clz, beanInstance);
            } else if (LmFilter.class.isAssignableFrom(clz) &&
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
     *
     * @param clz          类
     * @param beanInstance 类实例
     */
    @SuppressWarnings("unchecked")
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
                //拿到注解上Url集合
                String[] urls = requestMapping.value();
                //拿到注解上的请求方法
                LmRequest.LMHttpMethod requestMethod = requestMapping.method();
                //遍历url集合
                for (String url : urls) {
                    //创建一个Pair，该Pair是由请求方法以及MethodHandler构成的
                    Pair<LmRequest.LMHttpMethod, MethodHandler> pair =
                            new Pair<>(requestMethod,
                                    new DefaultMethodHandler(beanInstance, method));
                    if (httpHandlerMaps.get(url) == null) {
                        log.info("加载requestMapping : url is " + url);
                        //构造映射关系，如果是第一次碰到该URL，那么就创建一个新的List
                        httpHandlerMaps.put(url, Lists.newArrayList(pair));
                    } else {
                        //先检查是否有重复
                        checkRepeatMethod(url, requestMethod);
                        httpHandlerMaps.get(url).add(pair);
                    }
                }
            }
        }
    }

    /**
     * 检查是否有相同的路径和请求方法的元组已经存在于集合中了
     * @param url
     * @param requestMethod
     */
    private void checkRepeatMethod(String url, LmRequest.LMHttpMethod requestMethod) {
        httpHandlerMaps.get(url).forEach(pair -> {
            if (pair.first().equals(requestMethod)) {
                //如果有重复，直接抛出运行时异常，结束进程即可
                throw new RequestMethodRepeatException("同一个路径不得有重复的请求方法！ " +
                        "重复的请求方法是： " + requestMethod.getName()
                        + " 对应的路径是 : " + url);
            }
        });
    }


    /**
     * 处理Interceptor
     *
     * @param clz          类
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
     *
     * @param clz          类
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


    /**
     * 根据请求路径和请求方法 拿到对应的handler
     * @param url 请求路径
     * @param requestMethod 请求方法
     * @return handler
     */
    public static MethodHandler getHandler(String url, LmRequest.LMHttpMethod requestMethod) {
        if (getMethodAndMethodHandlerOfPath(url) == null) {
            return null;
        }
        //httpHandlerMaps.get(url)返回的是Set类型，遍历查找和请求方法对应的handler即可
        for (Pair<LmRequest.LMHttpMethod, MethodHandler> pair : httpHandlerMaps.get(url)) {
            if (pair.first().equals(requestMethod)) {
                return pair.second();
            }
        }
        return null;
    }

    public static List<Pair<LmRequest.LMHttpMethod, MethodHandler>> getMethodAndMethodHandlerOfPath(String url) {
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
