package top.yeonon.lmserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yeonon.lmserver.annotation.Controller;
import top.yeonon.lmserver.annotation.RequestMapping;
import top.yeonon.lmserver.utils.ClassUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author yeonon
 * @date 2018/5/23 0023 18:19
 **/
public class ControllerDiscover {

    private static final Logger log = LoggerFactory.getLogger(ControllerDiscover.class);

    private static final Map<String, HttpHandler> httpHandlerMaps = new HashMap<>();

    public static void doDiscover(String packageName) {

        Set<Class<?>> classSets = ClassUtil.getClassFromPackage(packageName);

        try {
            for (Class<?> clz : classSets) {
                if (clz.isAnnotationPresent(Controller.class)) {
                    log.info("加载controller ： " + clz.getName());
                    Object classInstance = clz.newInstance();
                    Method[] methods = clz.getDeclaredMethods();

                    for (Method method : methods) {
                        if (method != null && method.isAnnotationPresent(RequestMapping.class)) {
                            RequestMapping requestMapping = method.
                                    getAnnotation(RequestMapping.class);
                            String[] urls = requestMapping.value();
                            for (String url : urls) {
                                if (httpHandlerMaps.get(url) == null) {
                                    log.info("加载requestMapping : url is " + url);
                                    httpHandlerMaps.put(url, new HttpHandler(classInstance, method));
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

    public static HttpHandler getHandler(String url) {
        return httpHandlerMaps.get(url);
    }

}
