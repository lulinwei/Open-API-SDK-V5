package com.okex.open.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@EnableScheduling
@Component
public class Task {

    @Resource
    private GridService gridService;

    @Scheduled(fixedRate = 60000) // Execute every 60 seconds (1 minute)
    public void executeTask() {
        log.info("-----------------Task executed at: " + new java.util.Date());
        // Add your task logic here
        gridService.getGrid();
    }
}
