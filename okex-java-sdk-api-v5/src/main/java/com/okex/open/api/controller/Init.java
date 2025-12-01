package com.okex.open.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class Init {
    @Value("${ropeok.app}")
    private String data;

    @PostConstruct
    public void init() {
        // Your initialization code here
        System.out.println("Application started and initialization completed");
        // Check if the active profile is 'prod'

        log.info("Active profile: {}", data);
        if ("dev".equals(data)) {
            System.setProperty("proxyHost", "127.0.0.1");
            System.setProperty("proxyPort", "10809");
        }
    }
}
