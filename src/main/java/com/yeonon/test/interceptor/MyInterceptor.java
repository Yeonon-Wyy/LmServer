package com.yeonon.test.interceptor;

import top.yeonon.lmserver.annotation.Interceptor;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;
import top.yeonon.lmserver.interceptor.AbstractLmInterceptor;
import top.yeonon.lmserver.interceptor.LmInterceptor;

/**
 * @Author yeonon
 * @date 2018/5/25 0025 16:23
 **/
@Interceptor(value = "/test", order = 2)
public class MyInterceptor extends AbstractLmInterceptor {

    @Override
    public boolean preHandler(LmRequest request, LmResponse response) {
        return true;
    }

    @Override
    public void postHandler(LmRequest request, LmResponse response) {
        System.out.println("hello, 拦截器");
    }
}
