package top.yeonon.lmserver.core.ioc.discover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yeonon.lmserver.annotation.Interceptor;
import top.yeonon.lmserver.interceptor.LmInterceptor;
import top.yeonon.lmserver.utils.ClassUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author yeonon
 * @date 2018/5/25 0025 16:09
 **/
public class LmInterceptorDiscover implements Discover {

    private static final Logger log = LoggerFactory.getLogger(LmInterceptorDiscover.class);

    private static final Map<String, LmInterceptor> interceptorMaps = new HashMap<>();

    @Override
    public void doDiscover(String packageName) {
        Set<Class<?>> classSet = ClassUtil.getClassFromPackage(packageName);

        try {
            for (Class<?> clz : classSet) {
                if (clz != null && LmInterceptor.class.isAssignableFrom(clz) &&
                        clz.isAnnotationPresent(Interceptor.class)) {
                    LmInterceptor interceptorInstance = (LmInterceptor) clz.newInstance();
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
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public static LmInterceptor getInterceptor(String url) {
        return interceptorMaps.get(url);
    }
}
