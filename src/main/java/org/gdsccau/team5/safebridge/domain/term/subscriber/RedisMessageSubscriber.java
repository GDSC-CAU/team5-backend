package org.gdsccau.team5.safebridge.domain.term.subscriber;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final RedisManager redisManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // TODO Redis에서 데이터를 꺼내 Hot Term을 계산하고 Local Cache에 저장한다.
        LocalDateTime currentTime = LocalDateTime.now();

        Set<String> termFindTimeZSet = redisManager.getTermFindTimeZSet();

    }
}
