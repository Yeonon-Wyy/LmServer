package com.yeonon.test;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.yeonon.test.pojo.User;
import top.yeonon.lmserver.LmServerStarter;
import top.yeonon.lmserver.controller.TypeNameEnum;

/**
 * @Author yeonon
 * @date 2018/5/24 0024 19:48
 **/
public class Main {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        LmServerStarter.run(Main.class);
    }

    public static int test(int i) {
        return i;
    }
}
