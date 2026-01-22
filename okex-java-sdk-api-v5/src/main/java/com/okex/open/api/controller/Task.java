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
            okexApiList.add(new OkexApiUser("2598faab-8351-4e0d-8fd3-d91ea92bb693", "5E2CA393CF4466489F2FAFAF77FCD3F2", "Ropeok@123"));
            okexApiList.add(new OkexApiUser("2", "2", "2"));
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
