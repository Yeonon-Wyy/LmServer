package top.yeonon.lmserver.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yeonon.lmserver.annotation.Filter;
import top.yeonon.lmserver.controller.LmControllerDiscover;
import top.yeonon.lmserver.utils.ClassUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author yeonon
 * @date 2018/5/24 0024 16:24
 **/
public class LmFilterDiscover {

    private static final Logger log = LoggerFactory.getLogger(LmControllerDiscover.class);

    private static final Map<String, LmFilter> filterMaps = new HashMap<>();

    public static void doDiscover(String packageName) {
        Set<Class<?>> classSet = ClassUtil.getClassFromPackage(packageName);

        try {
            for (Class<?> clz : classSet) {
                if (clz != null && LmFilter.class.isAssignableFrom(clz) &&
                        clz.isAnnotationPresent(Filter.class)) {
                    LmFilter filterInstance = (LmFilter) clz.newInstance();
                    Filter filter = clz.getAnnotation(Filter.class);
                    String[] urls = filter.value();
                    for (String url : urls) {
                        if (filterMaps.get(url) == null) {
                            log.info("加载filter " + clz.getName() + " 要过滤的url是 ： " + url);
                            filterMaps.put(url, filterInstance);
                        } else {
                            log.info("已经加载过该url :" +  url + " 对应的filter");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public static LmFilter getFilter(String url) {
        return filterMaps.get(url);
    }

}
