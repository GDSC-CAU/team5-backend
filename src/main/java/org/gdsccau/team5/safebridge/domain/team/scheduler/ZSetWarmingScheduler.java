package org.gdsccau.team5.safebridge.domain.team.scheduler;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ZSetWarmingScheduler {

    private final UserTeamCheckService userTeamCheckService;
    private final RedisManager redisManager;

    @Scheduled(fixedRate = 300000) // 5분
    public void zSetWarming() {
        clearZSet();
        warmingZSet();
    }

    private void clearZSet() {
        userTeamCheckService.findAllUserIdWithTeam()
                .forEach(userId -> redisManager.clearZSet(redisManager.getZSetKey(userId)));
    }

    private void warmingZSet() {
        userTeamCheckService.findAllTeamOrderByLastChatTime()
                .forEach(data -> {
                    Long userId = data.getUserId();
                    Long teamId = data.getTeamId();
                    LocalDateTime lastChatTime = data.getLastChatTime();
                    redisManager.updateZSet(redisManager.getZSetKey(userId), teamId, lastChatTime);
                });
    }
}
