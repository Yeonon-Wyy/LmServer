package com.yeonon.test;

import top.yeonon.lmserver.LmServerStarter;

import java.io.File;

/**
 * @Author yeonon
 * @date 2018/5/24 0024 19:48
 **/
public class Main {

    public static void main(String[] args) {
        LmServerStarter.run(Main.class);

//        String path = Main.class.getResource("/").getPath() + "/test.html";
//        System.out.println(path);
//        File file = new File(path);
//        System.out.println(file.exists());
    }
}
