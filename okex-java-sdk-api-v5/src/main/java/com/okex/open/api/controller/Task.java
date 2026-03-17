package com.okex.open.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@EnableScheduling
@Component
public class Task {

    @Resource
    private GridService gridService;

    @Scheduled(fixedRate = 60000) // Execute every 60 seconds (1 minute)
    public void executeTask() {
        try {
            log.info("-----------------Task executed at: " + new java.util.Date());
            // Add your task logic here
            List<OkexApiUser> okexApiList = new ArrayList<>();
            okexApiList.add(new OkexApiUser("d71e7c87-79b6-4b1f-9355-dfd79c8f9fe1", "F99F7F4743F34BE24EC3204639BDC430", "Ropeok@123"));
//            okexApiList.add(new OkexApiUser("2", "2", "2"));
//            gridService.processForUser();
            for (OkexApiUser user : okexApiList) {
                try {
                    log.info("Processing task for user: {}", user.getApiKey());

                    // 为每个用户创建独立的GridService实例或设置用户上下文
                    gridService.processForUser(user);
                } catch (Exception e) {
                    log.error("Error processing task for user {}: ", user.getApiKey(), e);
                }
            }
        } catch (Exception e) {
            log.error("定时任务执行出错: ", e);
        }
    }
}
