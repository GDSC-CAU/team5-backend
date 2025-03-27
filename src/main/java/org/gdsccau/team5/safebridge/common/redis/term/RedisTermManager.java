package org.gdsccau.team5.safebridge.common.redis.term;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.util.RedisUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisTermManager {

    private final RedisTemplate<String, String> redisTemplate;

    public String getTermFindTimeZSetKey() {
        return "termFindTimeZSetKey";
    }

    public String getTermFindCountHashKey(final LocalDateTime chatTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(RedisUtil.HOUR_DATE_FORMAT);
        return chatTime.format(dateTimeFormatter);
    }

    public Set<String> getTermFindTimeZSet(final LocalDateTime findTime) {
        Double todayScore = RedisUtil.convertToDateFormat(RedisUtil.FULL_DATE_FORMAT, findTime);
        Double yesterdayScore = RedisUtil.convertToDateFormat(RedisUtil.FULL_DATE_FORMAT, findTime.minusDays(1));
        return redisTemplate.opsForZSet().reverseRangeByScore(getTermFindTimeZSetKey(), yesterdayScore, todayScore);
    }

    public Map<Object, Object> getTermFindCount(final LocalDateTime chatTime) {
        return redisTemplate.opsForHash().entries(getTermFindCountHashKey(chatTime));
    }

    public void updateTermFindTimeZSet(final String member, final LocalDateTime chatTime) {
        Double score = RedisUtil.convertToDateFormat(RedisUtil.FULL_DATE_FORMAT, chatTime);
        redisTemplate.opsForZSet().add(getTermFindTimeZSetKey(), member, score);
    }

    public void updateTermFindCountHash(final String field, final Integer count, final String findCountHashKey) {
        redisTemplate.opsForHash().increment(findCountHashKey, field, count);

        Long expireTime = redisTemplate.getExpire(findCountHashKey);
        if (expireTime <= 0) {
            redisTemplate.expire(findCountHashKey, Duration.ofHours(24)); // 24시간 뒤 자동 삭제
        }
    }
}
