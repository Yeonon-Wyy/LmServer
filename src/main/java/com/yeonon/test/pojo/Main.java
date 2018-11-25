package com.yeonon.test.pojo;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Author yeonon
 * @date 2018/7/20 0020 16:59
 **/
public class Main {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {


        System.out.println(System.getProperty("java.version"));
    }



    private void test(User user) {
        System.out.println(user.getId());
    }
}
