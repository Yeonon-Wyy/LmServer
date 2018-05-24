package com.top.test.controller.filter;

import top.yeonon.lmserver.annotation.Filter;
import top.yeonon.lmserver.filter.LmFilter;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

/**
 * @Author yeonon
 * @date 2018/5/24 0024 16:57
 **/
@Filter(value = {"/user", "/test"})
public class MyFilter implements LmFilter {

    @Override
    public void before(LmRequest request) {
        System.out.println("my filter");
    }

    @Override
    public void after(LmResponse response) {

    }
}
