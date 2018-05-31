package com.yeonon.test.service;

import top.yeonon.lmserver.annotation.Service;

/**
 * @Author yeonon
 * @date 2018/5/31 0031 21:25
 **/
@Service
public class MyService implements IMyService{

    public String testServeice() {
        return "testService";
    }
}
