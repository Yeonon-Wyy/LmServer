package com.yeonon.test.pojo;


/**
 * @Author yeonon
 * @date 2018/7/20 0020 16:33
 **/

public class User {

    private Long id;
    private String name;

    private Test test;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }
}
