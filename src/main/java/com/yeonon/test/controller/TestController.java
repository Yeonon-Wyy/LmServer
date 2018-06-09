package com.yeonon.test.controller;

import com.yeonon.test.interceptor.MyInterceptor;
import com.yeonon.test.service.IMyService;
import com.yeonon.test.service.MyService;
import top.yeonon.lmserver.annotation.Autowire;
import top.yeonon.lmserver.annotation.Controller;
import top.yeonon.lmserver.annotation.RequestMapping;
import top.yeonon.lmserver.http.LmRequest;
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
    public String test(LmRequest lmRequest, Long id) {
        System.out.println(lmRequest);
        System.out.println(id);
        return myService.testServeice();
    }
}
