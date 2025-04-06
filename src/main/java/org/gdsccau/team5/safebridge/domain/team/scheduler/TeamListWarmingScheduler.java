package org.gdsccau.team5.safebridge.domain.team.scheduler;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.userTeam.service.UserTeamQueryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeamListWarmingScheduler {

    private final UserTeamQueryService userTeamQueryService;
    private final RedisManager redisManager;

    @Scheduled(fixedRate = 300000) // 5분
    public void teamListWarming() {
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
                    LocalDateTime lastChatTime = data.getLastChatTime();
                    redisManager.updateTeamList(redisManager.getTeamListKey(userId), teamId, lastChatTime);
                });
        log.info("Team List Cache Warming!!");
    }
}
