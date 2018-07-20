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
    public String test(LmRequest lmRequest, Long id, LmResponse response) {
        System.out.println(lmRequest);
        System.out.println(id);
        System.out.println(response);
        return myService.testServeice();
    }

    @RequestMapping(value = "/user")
    public String user(int i) {
        User user = new User();

        return "Hello";
    }
}
