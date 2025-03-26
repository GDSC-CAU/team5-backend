package org.gdsccau.team5.safebridge.common.redis.term;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
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

    public String getTermFindTimeZSetKey() {
        return "termFindTimeZSetKey";
    }

    public String getTermFindCountHashKey(final LocalDateTime chatTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(RedisUtil.HOUR_DATE_FORMAT);
        return chatTime.format(dateTimeFormatter);
    }

    public Set<String> getTermFindTimeZSet() {
        Double todayScore = RedisUtil.convertToDateFormat(RedisUtil.FULL_DATE_FORMAT, LocalDateTime.now());
        Double yesterdayScore = RedisUtil.convertToDateFormat(RedisUtil.FULL_DATE_FORMAT, LocalDateTime.now().minusDays(1));
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
    }
}
