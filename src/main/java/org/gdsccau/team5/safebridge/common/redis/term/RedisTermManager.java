package org.gdsccau.team5.safebridge.common.redis.term;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.util.RedisUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisTermManager {

    private final RedisTemplate<String, String> redisTemplate;

    public String getTermFindCountHashKey(final LocalDateTime chatTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(RedisUtil.HOUR_DATE_FORMAT);
        return chatTime.format(dateTimeFormatter);
    }

    public Map<Object, Object> getTermFindCount(final LocalDateTime chatTime) {
        return redisTemplate.opsForHash().entries(getTermFindCountHashKey(chatTime));
    }

    public void updateTermFindCountHash(final String field, final Integer count, final String findCountHashKey) {
        redisTemplate.opsForHash().increment(findCountHashKey, field, count);

        Long expireTime = redisTemplate.getExpire(findCountHashKey);
        if (expireTime <= 0) {
            redisTemplate.expire(findCountHashKey, Duration.ofHours(24)); // 24시간 뒤 자동 삭제
        }
    }
}
