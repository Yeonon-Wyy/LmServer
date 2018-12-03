package top.yeonon.lmserver.web.method;

import top.yeonon.lmserver.web.http.LmRequest;
import top.yeonon.lmserver.web.http.LmResponse;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author yeonon
 * @date 2018/11/27 0027 14:30
 **/
public interface MethodHandler {

    /**
     * 调用方法,并产生返回值
     *
     * @param request 这个参数是必须的
     * @return 调用method.invoke()的返回值
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException    IllegalAccessException
     */
    Object execute(LmRequest request, LmResponse response)
            throws InvocationTargetException, IllegalAccessException;
}
