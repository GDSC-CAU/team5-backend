package org.gdsccau.team5.safebridge.domain.team.scheduler;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.userTeam.service.UserTeamQueryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ZSetWarmingScheduler {

    private final UserTeamQueryService userTeamQueryService;
    private final RedisManager redisManager;

    @Scheduled(fixedRate = 300000) // 5분
    public void zSetWarming() {
        clearTeamList();
        warmingTeamList();
    }

    private void clearTeamList() {
        userTeamQueryService.findAllUserIdWithTeam()
                .forEach(userId -> redisManager.clearTeamList(redisManager.getTeamListKey(userId)));
    }

    private void warmingTeamList() {
        userTeamQueryService.findAllTeamOrderByLastChatTime()
                .forEach(data -> {
                    Long userId = data.getUserId();
                    Long teamId = data.getTeamId();
                    LocalDateTime lastChatTime =
                            data.getLastChatTime() == null ? LocalDateTime.now() : data.getLastChatTime();
                    redisManager.updateTeamList(redisManager.getTeamListKey(userId), teamId, lastChatTime);
                });
    }
}
