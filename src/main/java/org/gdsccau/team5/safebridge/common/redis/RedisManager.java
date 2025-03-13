package org.gdsccau.team5.safebridge.common.redis;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisManager {

    private final RedisTemplate<String, String> redisTemplate;

    public String getInRoomKey(final Long userId, final Long teamId) {
        return "userId:" + userId + ":teamId:" + teamId + ":inRoom";
    }

    public String getUnReadMessageKey(final Long userId, final Long teamId) {
        return "userId:" + userId + ":teamId:" + teamId + ":unReadMessage";
    }

    public String getZSetKey(final Long userId) {
        return "userId:" + userId + ":team";
    }

    public String getTranslatedTermKey(final Long termId, final Language language) {
        return "termId:" + termId + ":language:" + language.getCode() + ":translated";
    }

    public int getInRoom(final String inRoomKey) {
        String inRoomValue = redisTemplate.opsForValue().get(inRoomKey);
        return inRoomValue != null ? Integer.parseInt(inRoomValue) : 0;
    }

    public int getUnReadMessage(final String unReadMessageKey) {
        String unReadMessageValue = redisTemplate.opsForValue().get(unReadMessageKey);
        return unReadMessageValue != null ? Integer.parseInt(unReadMessageValue) : 0;
    }

    public void updateUnReadMessage(final String unReadMessageKey) {
        redisTemplate.opsForValue().increment(unReadMessageKey, 1);
    }

    public Set<String> getZSet(final String zSetKey) {
        // TODO Redis 서버가 다운되었을 때, DB에서 각 채팅방의 마지막 채팅 createdAt을 이용해 데이터를 로드한다.
        return redisTemplate.opsForZSet().reverseRange(zSetKey, 0, -1);
    }

    public void updateZSet(final String zSetKey, final Long teamId, final Chat chat) {
        long score = chat.getCreatedAt()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli();
        redisTemplate.opsForZSet().add(zSetKey, String.valueOf(teamId), score);
    }

    public String getTranslatedTerm(final String translatedTermKey) {
        return redisTemplate.opsForValue().get(translatedTermKey);
    }

    public void updateTranslatedTerm(final String translatedTermKey, final String translatedTerm) {
        redisTemplate.opsForValue().set(translatedTermKey, translatedTerm);
    }

    public void initRedis(final Long userId, final Long teamId) {
        String inRoomKey = this.getInRoomKey(userId, teamId);
        String unReadMessageKey = this.getUnReadMessageKey(userId, teamId);
        String zSetKey = this.getZSetKey(userId);

        long score = LocalDateTime.now()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli();
        redisTemplate.opsForValue().set(inRoomKey, "0");
        redisTemplate.opsForValue().set(unReadMessageKey, "0");
        redisTemplate.opsForZSet().add(zSetKey, String.valueOf(teamId), score);
    }

    public void updateRedisWhenJoin(final Long userId, final Long teamId) {
        String inRoomKey = this.getInRoomKey(userId, teamId);
        String unReadMessageKey = this.getUnReadMessageKey(userId, teamId);
        redisTemplate.opsForValue().set(inRoomKey, "1");
        redisTemplate.opsForValue().set(unReadMessageKey, "0");
    }

    public void updateRedisWhenLeave(final Long userId, final Long teamId) {
        String inRoomKey = this.getInRoomKey(userId, teamId);
        String unReadMessageKey = this.getUnReadMessageKey(userId, teamId);
        redisTemplate.opsForValue().set(inRoomKey, "0");
        redisTemplate.opsForValue().set(unReadMessageKey, "0");
    }

    public void updateRedisWhenDelete(final Long userId, final Long teamId) {
        String inRoomKey = this.getInRoomKey(userId, teamId);
        String unReadMessageKey = this.getUnReadMessageKey(userId, teamId);
        String zSetKey = this.getZSetKey(userId);
        redisTemplate.delete(inRoomKey);
        redisTemplate.delete(unReadMessageKey);
        redisTemplate.opsForZSet().remove(zSetKey, String.valueOf(teamId)); // zSet에서 특정 teamId만 삭제하기
    }
}
