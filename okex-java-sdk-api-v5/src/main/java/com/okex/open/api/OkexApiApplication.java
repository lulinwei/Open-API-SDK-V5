package com.okex.open.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OkexApiApplication {
    public static void main(String[] args) {

         System.setProperty("proxyHost", "127.0.0.1");
        System.setProperty("proxyPort", "10809");


        SpringApplication.run(OkexApiApplication.class, args);
    }
}
