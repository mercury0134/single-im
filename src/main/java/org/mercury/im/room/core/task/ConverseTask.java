package org.mercury.im.room.core.task;

import jakarta.annotation.Resource;
import org.mercury.im.room.service.ConverseDbService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ConverseTask {

    @Resource
    private ConverseDbService converseDbService;

    /**
     * 会话列表记录 每天凌晨2点清理
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void clearConverseList() {
        // 获取 userIds 列表 rpc?
        // 简化操作 直接前缀搜索

        converseDbService.userConverseTask("1");
    }

}
