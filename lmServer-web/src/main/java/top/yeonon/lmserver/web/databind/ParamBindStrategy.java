package top.yeonon.lmserver.web.databind;


import top.yeonon.lmserver.web.http.LmWebRequest;

import java.lang.reflect.Method;

/**
 * 参数绑定接口
 * @Author yeonon
 * @date 2018/8/9 0009 17:45
 **/
public interface ParamBindStrategy {

    /**
     * 执行策略
     * @param method 方法实例
     * @param instance 该方法所属的类实例
     * @param webRequest 包装后的请求
     * @return 参数数组
     */
    Object[] execute(Method method, Object instance, LmWebRequest webRequest);
}
