package com.sun.flow.bean;

import org.springframework.stereotype.Component;

@Component
public class MyBean {

    public String getAssignee(){
        System.out.println("getAssignee() 执行了");
        return "王五";
    }
}
