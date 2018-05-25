package com.yeonon.test.controller;

import top.yeonon.lmserver.annotation.Controller;
import top.yeonon.lmserver.annotation.RequestMapping;
import top.yeonon.lmserver.http.LmRequest;

/**
 * @Author yeonon
 * @date 2018/5/25 0025 16:24
 **/
@Controller
public class TestController {


    @RequestMapping(value = "/test")
    public String test(LmRequest lmRequest) {
        return "test";
    }
}
