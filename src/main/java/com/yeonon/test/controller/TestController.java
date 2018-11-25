package com.yeonon.test.controller;

import com.yeonon.test.interceptor.MyInterceptor;
import com.yeonon.test.pojo.User;
import com.yeonon.test.service.IMyService;
import com.yeonon.test.service.MyService;
import top.yeonon.lmserver.annotation.Autowire;
import top.yeonon.lmserver.annotation.Controller;
import top.yeonon.lmserver.annotation.RequestMapping;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;
import top.yeonon.lmserver.interceptor.LmInterceptor;

/**
 * @Author yeonon
 * @date 2018/5/25 0025 16:24
 **/
@Controller
public class TestController {

    @Autowire
    private MyService myService;

    @RequestMapping(value = "/test")
    public Long test(LmRequest lmRequest, Long id, LmResponse response) {

        return id;
    }

    @RequestMapping(value = "/user")
    public User user(User user, LmRequest request, LmResponse response) {
        return user;
    }


}
