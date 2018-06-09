package com.yeonon.test.filter;

import top.yeonon.lmserver.annotation.Filter;
import top.yeonon.lmserver.filter.AbstractLmFilter;
import top.yeonon.lmserver.filter.LmFilter;
import top.yeonon.lmserver.http.LmRequest;
import top.yeonon.lmserver.http.LmResponse;

/**
 * @Author yeonon
 * @date 2018/5/30 0030 20:38
 **/
@Filter(value = "/test")
public class MyFilter extends AbstractLmFilter {

    @Override
    public void before(LmRequest request) {
//        System.out.println("请求之前");
    }

    @Override
    public void after(LmResponse response) {
//        System.out.println("请求之后");
    }
}
