package com.top.test.controller.pojo;

/**
 * @Author yeonon
 * @date 2018/5/23 0023 19:40
 **/
public class User {

    private Long id;

    private String name;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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
}
