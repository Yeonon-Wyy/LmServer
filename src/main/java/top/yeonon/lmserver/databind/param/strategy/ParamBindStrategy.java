package top.yeonon.lmserver.databind.param.strategy;


import top.yeonon.lmserver.http.LmWebRequest;

import java.lang.reflect.Method;

/**
 * @Author yeonon
 * @date 2018/8/9 0009 17:45
 **/
public interface ParamBindStrategy {

    Object[] execute(Method method, Object instance, LmWebRequest webRequest);
}
