package com.yeonon.test.service;

import com.yeonon.test.controller.MyController;
import com.yeonon.test.controller.TestController;
import com.yeonon.test.filter.TestBean;
import top.yeonon.lmserver.annotation.Autowire;
import top.yeonon.lmserver.annotation.Service;

/**
 * @Author yeonon
 * @date 2018/5/31 0031 21:25
 **/
@Service
public class MyService implements IMyService{

    @Autowire
    private TestBean testBean;

    public String testServeice() {
        return testBean.getTest();
    }
}
