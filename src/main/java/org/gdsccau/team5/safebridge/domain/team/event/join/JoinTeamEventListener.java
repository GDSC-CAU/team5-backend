package org.gdsccau.team5.safebridge.domain.team.event.join;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class JoinTeamEventListener {

    private final RedisManager redisManager;

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleJoinTeamEvent(JoinTeamEvent event) {
        try {
            redisManager.updateRedisWhenJoin(event.getUserId(), event.getTeamId(), event.getDbLookUp());
        } catch (Exception e) {
            log.error("Join Team Event 실패", e);
            // TODO 예외처리 ex) 재시도
        }
    }
}
