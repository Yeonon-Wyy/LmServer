package com.yeonon.test.config;

import com.yeonon.test.filter.TestBean;
import top.yeonon.lmserver.annotation.Bean;
import top.yeonon.lmserver.annotation.Configuration;

/**
 * @Author yeonon
 * @date 2018/6/26 0026 20:28
 **/
@Configuration
public class MyConfig {

    @Bean
    public TestBean testBean() {
        TestBean testBean = new TestBean();
        testBean.setTest("我去");
        return testBean;
    }
}
