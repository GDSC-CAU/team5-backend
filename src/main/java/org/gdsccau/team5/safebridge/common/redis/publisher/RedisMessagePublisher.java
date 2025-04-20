package org.gdsccau.team5.safebridge.common.redis.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisMessagePublisher {

    private final RedisTemplate<String, String> redisTemplate;

    public void publish(String channel, String message) {
        log.info("Publishing message {} to channel {}", message, channel);
        redisTemplate.convertAndSend(channel, message);
    }
}
