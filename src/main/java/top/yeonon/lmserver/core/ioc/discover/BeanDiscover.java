package top.yeonon.lmserver.core.ioc.discover;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yeonon.lmserver.annotation.*;
import top.yeonon.lmserver.controller.LmHttpHandler;
import top.yeonon.lmserver.filter.LmFilter;
import top.yeonon.lmserver.interceptor.LmInterceptor;
import top.yeonon.lmserver.utils.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author yeonon
 * @date 2018/5/31 0031 19:37
 **/
public class BeanDiscover implements Discover {

    private static final Logger log = LoggerFactory.getLogger(BeanDiscover.class);

    //bean maps
    private static final Map<Class<?>, Object> beanMaps = new HashMap<>();

    //http handler maps
    private static final Map<String, LmHttpHandler> httpHandlerMaps = new HashMap<>();

    //filter maps
    private static final Map<String, List<LmFilter> > filterMaps = new HashMap<>();

    //interceptor maps
    private static final Map<String, LmInterceptor> interceptorMaps = new HashMap<>();



    @Override
    public void doDiscover(String packageName) {
        Set<Class<?>> classSets = ClassUtil.getClassFromPackage(packageName);

        try {
            for (Class<?> clz : classSets) {
                if (clz != null && clz.getAnnotations() != null) {
                    Annotation[] annotations = clz.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().isAnnotationPresent(Component.class)) {
                            beanMaps.put(clz, clz.newInstance());
                        }
                    }
                }
            }

            processBean();

        } catch (IllegalAccessException | InstantiationException e) {
            log.error(e.toString());
        }
    }

    private void processBean() {

        beanMaps.forEach((clz, beanInstance) -> {
            if (LmInterceptor.class.isAssignableFrom(clz) &&
                    clz.isAnnotationPresent(Interceptor.class)) {
                processInterceptor(clz, beanInstance);
            }
            else if (LmFilter.class.isAssignableFrom(clz) &&
                    clz.isAnnotationPresent(Filter.class)) {
                processFilter(clz, beanInstance);
            } else if (clz.isAnnotationPresent(Controller.class)) {
                processController(clz, beanInstance);
            }
        });

        //处理filter顺序
        filterMaps.forEach((url, filters) -> {
            Collections.sort(filters);
        });
    }

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


    private void processInterceptor(Class<?> clz, Object beanInstance) {
        LmInterceptor interceptorInstance = (LmInterceptor) beanInstance;
        Interceptor interceptor = clz.getAnnotation(Interceptor.class);
        String[] urls = interceptor.value();
        for (String url : urls) {
            if (interceptorMaps.get(url) == null) {
                log.info("加载interface " + clz.getName() + "url 是" + url);
                interceptorMaps.put(url, interceptorInstance);
            } else {
                log.info("该url " + url + " 已经被加载过了");
            }
        }
    }

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



    public static Map<Class<?>, Object> getBeanMaps() {
        return beanMaps;
    }

    public static LmHttpHandler getHandler(String url) {
        return httpHandlerMaps.get(url);
    }

    public static List<LmFilter> getFilter(String url) {
        return filterMaps.get(url);
    }

    public static LmInterceptor getInterceptor(String url) {
        return interceptorMaps.get(url);
    }
}
