package org.gdsccau.team5.safebridge.domain.team.event.leave;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.team.event.join.JoinTeamEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaveTeamEventListener {

    private final RedisManager redisManager;

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLeaveTeamEvent(JoinTeamEvent event) {
        try {
            redisManager.updateRedisWhenLeave(event.getUserId(), event.getTeamId(), event.getDbLookUp());
        } catch (Exception e) {
            log.error("Leave Team Event 실패", e);
            // TODO 예외처리 ex) 재시도
        }
    }
}
