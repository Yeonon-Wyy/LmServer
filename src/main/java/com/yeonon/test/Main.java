package com.yeonon.test;

import com.yeonon.test.interceptor.MyFilter1;
import top.yeonon.lmserver.LmServerStarter;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @Author yeonon
 * @date 2018/5/24 0024 19:48
 **/
public class Main {

    public static void main(String[] args) {
        LmServerStarter.run(Main.class);
    }
}
