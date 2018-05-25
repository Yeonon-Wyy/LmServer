package com.yeonon.test.interceptor;

import top.yeonon.lmserver.annotation.Interceptor;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;
import top.yeonon.lmserver.interceptor.LmInterceptor;

/**
 * @Author yeonon
 * @date 2018/5/25 0025 16:23
 **/
@Interceptor(value = "/test")
public class MyInterceptor implements LmInterceptor {
    @Override
    public boolean doInterceptor(LmRequest request, LmResponse response) {
//        response.setContent("被拦截了").send();
        return true;
    }
}
