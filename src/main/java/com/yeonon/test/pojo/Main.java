package com.yeonon.test.pojo;

import top.yeonon.lmserver.core.ioc.DefaultBeanProcessor;

import javax.jws.soap.SOAPBinding;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author yeonon
 * @date 2018/7/20 0020 16:59
 **/
public class Main {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        Method method = Main.class.getDeclaredMethod("test", User.class);
        Class<?> type = User.class;
        Object instance = type.newInstance();
        Object object = new Object();
        for (Field field : type.getDeclaredFields()) {
            System.out.println(field.getType().getTypeName());
            StringBuilder builder = new StringBuilder(field.getType().getTypeName());

            field.set(instance, object);
        }


    }

    private static <T> T process(Class<T> clz) {
        try {
            return clz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void test(User user) {
        System.out.println(user.getId());
    }
}
