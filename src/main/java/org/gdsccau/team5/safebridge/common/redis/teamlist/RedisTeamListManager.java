package org.gdsccau.team5.safebridge.common.redis.teamlist;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.util.RedisUtil;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisTeamListManager {

    private final RedisTemplate<String, String> redisTemplate;

    public void initTeamList(final String teamListKey, final Long teamId) {
        Double score = RedisUtil.convertToDateFormat(RedisUtil.FULL_DATE_FORMAT, LocalDateTime.now());
        redisTemplate.opsForZSet().add(teamListKey, String.valueOf(teamId), score);
        redisTemplate.expire(teamListKey, RedisUtil.TTL, TimeUnit.HOURS);
    }

    public String getTeamListKey(final Long userId) {
        return "userId:" + userId + ":team";
    }

    public Boolean hasTeamList(final String teamListKey) {
        return redisTemplate.hasKey(teamListKey);
    }

    public Set<String> getTeamList(final String teamListKey) {
        return redisTemplate.opsForZSet().reverseRange(teamListKey, 0, -1);
    }

    public void updateTeamList(final String teamListKey, final Long teamId, final LocalDateTime lastChatTime) {
        Double score = RedisUtil.convertToDateFormat(RedisUtil.FULL_DATE_FORMAT, lastChatTime);
        redisTemplate.opsForZSet().add(teamListKey, String.valueOf(teamId), score);
        redisTemplate.expire(teamListKey, RedisUtil.TTL, TimeUnit.HOURS);
    }

    public void deleteTeamList(final String teamListKey, final Long teamId) {
        redisTemplate.opsForZSet().remove(teamListKey, String.valueOf(teamId));
    }
}
