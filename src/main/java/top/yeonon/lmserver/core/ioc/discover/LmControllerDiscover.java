package top.yeonon.lmserver.core.ioc.discover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yeonon.lmserver.annotation.Controller;
import top.yeonon.lmserver.annotation.RequestMapping;
import top.yeonon.lmserver.controller.LmHttpHandler;
import top.yeonon.lmserver.utils.ClassUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * 用于发现应用程序中包含controller和requestMapping注解的类
 * @Author yeonon
 * @date 2018/5/23 0023 18:19
 **/
public class LmControllerDiscover implements Discover {

    //log4j日志
    private static final Logger log = LoggerFactory.getLogger(LmControllerDiscover.class);

    //保存 url -> httpHandler 的映射关系的Map
    private static final Map<String, LmHttpHandler> httpHandlerMaps = new HashMap<>();

    /**
     * 调用该方法才会去扫描整个包
     * @param packageName 包名
     */
    @Override
    public void doDiscover(String packageName) {
        //扫描整个包（根据包名），返回的是类的集合（class对象）
        Set<Class<?>> classSets = ClassUtil.getClassFromPackage(packageName);
        try {
            //遍历类集合
            for (Class<?> clz : classSets) {
                //如果该类上有Controller注解
                if (clz != null && clz.isAnnotationPresent(Controller.class)) {
                    log.info("加载controller ： " + clz.getName());
                    //实例化该类
                    Object classInstance = clz.newInstance();
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
                                    httpHandlerMaps.put(url, new LmHttpHandler(classInstance, method));
                                } else {
                                    log.info("已经做过该Url的映射");
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }

    }

    /**
     * 根据url获得HttpHandler对象
     * @param url url
     * @return LmHttpHandler(可能为null)
     */
    public static LmHttpHandler getHandler(String url) {
        return httpHandlerMaps.get(url);
    }

}
