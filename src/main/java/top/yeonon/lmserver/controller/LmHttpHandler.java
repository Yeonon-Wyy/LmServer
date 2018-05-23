package top.yeonon.lmserver.controller;

import top.yeonon.lmserver.http.LmRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 处理请求的类
 *
 * Ps： 一个类可能有多个处理Url的方法，即一个classInstance可能有多个method，这种情况也会有多个handler，
 * 但是他们的method字段不同(classInstance相同)。
 * @Author yeonon
 * @date 2018/5/23 0023 18:24
 **/
public class LmHttpHandler {
    //类的一个实例
    private Object classInstance;

    //映射的方法
    private Method method;

    /**
     *
     * @param classInstance 实例
     * @param method 处理映射的方法
     */
    public LmHttpHandler(Object classInstance, Method method) {
        this.classInstance = classInstance;
        this.method = method;
    }

    /**
     * 调用方法,并产生返回值
     * @param request 这个参数是必须的
     * @return 调用method.invoke()的返回值
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException IllegalAccessException
     */
    public Object execute(LmRequest request) throws InvocationTargetException, IllegalAccessException {
        Object res = null;
        method.setAccessible(true);
        res = this.method.invoke(this.classInstance, request);
        return res;
    }
}
