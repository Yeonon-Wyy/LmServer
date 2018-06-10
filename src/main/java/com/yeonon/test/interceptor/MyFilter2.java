package com.yeonon.test.interceptor;

import top.yeonon.lmserver.annotation.Filter;
import top.yeonon.lmserver.filter.AbstractLmFilter;
import top.yeonon.lmserver.filter.LmFilter;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

/**
 * @Author yeonon
 * @date 2018/6/9 0009 14:38
 **/
@Filter(value = "/test",order = 2)
public class MyFilter2 extends AbstractLmFilter {

    @Override
    public void doFilter(LmRequest request) {
        System.out.println("filter 2 before");
    }

}
