package com.yourapp.hello_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/hello")
public class HelloController {
    @GetMapping
    public String getHello(){
        return "hello";
    }
}