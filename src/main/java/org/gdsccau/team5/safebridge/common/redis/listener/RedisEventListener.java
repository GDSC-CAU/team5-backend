package org.gdsccau.team5.safebridge.common.redis.listener;

import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.team.event.TeamCreateEvent;
import org.gdsccau.team5.safebridge.domain.team.event.TeamDeleteEvent;
import org.gdsccau.team5.safebridge.domain.team.event.TeamJoinEvent;
import org.gdsccau.team5.safebridge.domain.team.event.TeamLeaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RedisEventListener {

    private final RedisManager redisManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTeamCreated(TeamCreateEvent event) {
        event.getUserIds().forEach(userId -> redisManager.initRedis(userId, event.getTeamId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTeamDeleted(TeamDeleteEvent event) {
        event.getUserIds().forEach(userId -> redisManager.updateRedisWhenDelete(userId, event.getTeamId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTeamJoin(TeamJoinEvent event) {
        redisManager.updateRedisWhenJoin(event.getUserId(), event.getTeamId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTeamLeave(TeamLeaveEvent event) {
        redisManager.updateRedisWhenLeave(event.getUserId(), event.getTeamId());
    }
}
