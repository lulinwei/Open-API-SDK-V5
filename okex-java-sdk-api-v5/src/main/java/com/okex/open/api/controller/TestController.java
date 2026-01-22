package com.okex.open.api.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Resource
    GridService gridService;
    @GetMapping("/hello")
    public String hello() {


//        gridService.getGrid();

        return "Hello, OKEX API!";
    }


}
