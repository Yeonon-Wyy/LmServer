package com.top.test.controller;

import com.top.test.controller.pojo.User;
import org.apache.commons.lang3.StringUtils;
import top.yeonon.lmserver.annotation.Controller;
import top.yeonon.lmserver.annotation.RequestMapping;
import top.yeonon.lmserver.http.LmRequest;

/**
 * @Author yeonon
 * @date 2018/5/22 0022 22:22
 **/
@Controller
public class TestController {

    @RequestMapping("/test")
    public String test(LmRequest request) {
        return "test,hello";
    }

    @RequestMapping("/user")
    public User getUser(LmRequest request) {
        Long id = Long.valueOf((String) request.getParam("id"));
        String name = (String) request.getParam("name");

        if (id == null || name == null) {
            return null;
        }
        return new User(id, name);
    }
}
