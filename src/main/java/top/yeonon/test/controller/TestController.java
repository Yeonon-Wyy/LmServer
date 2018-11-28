package top.yeonon.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import top.yeonon.lmserver.core.annotation.Autowire;
import top.yeonon.lmserver.core.annotation.Controller;
import top.yeonon.lmserver.core.annotation.RequestMapping;
import top.yeonon.lmserver.core.http.LmRequest;


/**
 * @Author yeonon
 * @date 2018/11/25 0025 17:26
 **/
@Controller
public class TestController {


    @RequestMapping(value = "/test",method = LmRequest.LMHttpMethod.GET)
    public Long test(Long id) {
        return id;
    }

    @RequestMapping(value = "/test", method = LmRequest.LMHttpMethod.POST)
    public Long test1(Long id) {
        System.out.println("post method");
        return id;
    }

    @RequestMapping(value = "/test", method = LmRequest.LMHttpMethod.PUT)
    public Long test3(Long id) {
        System.out.println("put method");
        return id;
    }

    @RequestMapping(value = "/test", method = LmRequest.LMHttpMethod.DELETE)
    public Long test4(Long id) {
        System.out.println("delete method");
        return id;
    }


    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/string")
    public String myString(String str) {
        return str;
    }
}
