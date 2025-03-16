package org.gdsccau.team5.safebridge.common.redis;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
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

    private final static Integer TTL = 9;
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

    public int getInRoomOrDefault(final String inRoomKey, final Supplier<Integer> dbLookUp) {
        if (isInRoomExists(inRoomKey)) {
            return updateInRoomWithPER(inRoomKey, dbLookUp);
        }
        return updateInRoomWithoutPER(inRoomKey, dbLookUp);
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
        String inRoomKey = getInRoomKey(userId, teamId);
        String unReadMessageKey = getUnReadMessageKey(userId, teamId);
        String zSetKey = getZSetKey(userId);

        initInRoom(inRoomKey);
        initUnReadMessage(unReadMessageKey);
        initZSet(zSetKey, teamId);
    }

    public void updateRedisWhenJoin(final Long userId, final Long teamId, final Supplier<Integer> dbLookUp) {
        String inRoomKey = getInRoomKey(userId, teamId);
        String unReadMessageKey = getUnReadMessageKey(userId, teamId);
        updateInRoomWithoutPER(inRoomKey, dbLookUp);
        redisTemplate.opsForValue().set(unReadMessageKey, "0", TTL, TimeUnit.HOURS);
    }

    public void updateRedisWhenLeave(final Long userId, final Long teamId, final Supplier<Integer> dbLookUp) {
        String inRoomKey = getInRoomKey(userId, teamId);
        String unReadMessageKey = getUnReadMessageKey(userId, teamId);
        updateInRoomWithoutPER(inRoomKey, dbLookUp);
        redisTemplate.opsForValue().set(unReadMessageKey, "0", TTL, TimeUnit.HOURS);
    }

    public void updateRedisWhenDelete(final Long userId, final Long teamId) {
        String inRoomKey = getInRoomKey(userId, teamId);
        String unReadMessageKey = getUnReadMessageKey(userId, teamId);
        String zSetKey = getZSetKey(userId);
        redisTemplate.delete(inRoomKey);
        redisTemplate.delete(inRoomKey + ":delta");
        redisTemplate.delete(inRoomKey + ":expiry");
        redisTemplate.delete(unReadMessageKey);
        redisTemplate.opsForZSet().remove(zSetKey, String.valueOf(teamId));
    }

    private Boolean isInRoomExists(final String inRoomKey) {
        return redisTemplate.hasKey(inRoomKey);
    }

    private int getInRoom(final String inRoomKey) {
        String inRoomValue = redisTemplate.opsForValue().get(inRoomKey);
        return inRoomValue != null ? Integer.parseInt(inRoomValue) : 0;
    }

    private long getInRoomDelta(final String inRoomKey) {
        String inRoomDeltaValue = redisTemplate.opsForValue().get(inRoomKey + ":delta");
        return inRoomDeltaValue != null ? Long.parseLong(inRoomDeltaValue) : 0;
    }

    private long getInRoomExpiry(final String inRoomKey) {
        String inRoomExpiry = redisTemplate.opsForValue().get(inRoomKey + ":expiry");
        return inRoomExpiry != null ? Long.parseLong(inRoomExpiry) : 0;
    }

    private long getCurrentTime() {
        return LocalDateTime.now()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli();
    }

    private void initInRoom(final String inRoomKey) {
        long expiryTimestamp = getCurrentTime() + TimeUnit.HOURS.toMillis(TTL);
        redisTemplate.opsForValue().set(inRoomKey, "0", TTL, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(inRoomKey + ":delta", "0", TTL, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(inRoomKey + ":expiry", String.valueOf(expiryTimestamp), TTL, TimeUnit.HOURS);
    }

    private void initUnReadMessage(final String unReadMessageKey) {
        redisTemplate.opsForValue().set(unReadMessageKey, "0", TTL, TimeUnit.HOURS);
    }

    private void initZSet(final String zSetKey, final Long teamId) {
        long score = LocalDateTime.now()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli();
        redisTemplate.opsForZSet().add(zSetKey, String.valueOf(teamId), score);
        redisTemplate.expire(zSetKey, TTL, TimeUnit.HOURS);
    }

    private int updateInRoomWithoutPER(final String inRoomKey, final Supplier<Integer> dbLookUp) {
        long nowTime = getCurrentTime();
        int inRoom = dbLookUp.get();
        long delta = getCurrentTime() - nowTime;
        saveInRoom(inRoomKey, inRoom, delta);
        return inRoom;
    }

    // PER Algorithm
    private int updateInRoomWithPER(final String inRoomKey, final Supplier<Integer> dbLookUp) {
        long delta = getInRoomDelta(inRoomKey);
        long expiry = getInRoomExpiry(inRoomKey);
        double beta = 1.0;

        long perGap = (long) (delta * beta * Math.log10(Math.random()));
        if (getCurrentTime() - perGap >= expiry) {
            long nowTime = getCurrentTime();
            int newInRoom = dbLookUp.get();
            long newDelta = getCurrentTime() - nowTime;
            saveInRoom(inRoomKey, newInRoom, newDelta);
            return newInRoom;
        }
        return getInRoom(inRoomKey);
    }

    private void saveInRoom(final String inRoomKey, final int inRoom, final long delta) {
        long expiryTimestamp = getCurrentTime() + TimeUnit.HOURS.toMillis(TTL);
        redisTemplate.opsForValue().set(inRoomKey, String.valueOf(inRoom), TTL, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(inRoomKey + ":delta", String.valueOf(delta), TTL, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(inRoomKey + ":expiry", String.valueOf(expiryTimestamp), TTL, TimeUnit.HOURS);
    }
}
