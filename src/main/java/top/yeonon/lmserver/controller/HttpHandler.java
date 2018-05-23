package top.yeonon.lmserver.controller;

import top.yeonon.lmserver.http.LmRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author yeonon
 * @date 2018/5/23 0023 18:24
 **/
public class HttpHandler {
    private Object classInstance;
    private Method method;

    public HttpHandler(Object classInstance, Method method) {
        this.classInstance = classInstance;
        this.method = method;
    }

    public Object execute(LmRequest request) throws InvocationTargetException, IllegalAccessException {
        Object res = null;
        method.setAccessible(true);
        res = this.method.invoke(this.classInstance, request);
        return res;
    }
}
