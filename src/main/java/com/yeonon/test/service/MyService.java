package com.yeonon.test.service;

import com.yeonon.test.controller.MyController;
import com.yeonon.test.controller.TestController;
import top.yeonon.lmserver.annotation.Autowire;
import top.yeonon.lmserver.annotation.Service;

/**
 * @Author yeonon
 * @date 2018/5/31 0031 21:25
 **/
@Service
public class MyService implements IMyService{

    @Autowire
    private TestController controller;

    public String testServeice() {
        System.out.println(controller);
        return "testService";
    }
}
