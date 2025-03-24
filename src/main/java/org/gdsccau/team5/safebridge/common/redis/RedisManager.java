package org.gdsccau.team5.safebridge.common.redis;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisManager {

    private final static Integer TTL = 6;
    private final RedisTemplate<String, String> redisTemplate;

    // InRoom
    public String getInRoomKey(final Long userId, final Long teamId) {
        return "userId:" + userId + ":teamId:" + teamId + ":inRoom";
    }

    public int getInRoomOrDefault(final String inRoomKey, final Supplier<Integer> dbLookUp) {
        if (isInRoomExists(inRoomKey)) {
            return getInRoom(inRoomKey);
        }
        int inRoom = dbLookUp.get();
        updateInRoom(inRoomKey, inRoom);
        return inRoom;
    }

    // unReadMessage
    public String getUnReadMessageKey(final Long userId, final Long teamId) {
        return "userId:" + userId + ":teamId:" + teamId + ":unReadMessage";
    }

    public int getUnReadMessage(final String unReadMessageKey, final Supplier<Integer> dbLookUp) {
        if (isUnReadMessageExists(unReadMessageKey)) {
            return getUnReadMessage(unReadMessageKey);
        }
        int unReadMessage = dbLookUp.get();
        updateUnReadMessage(unReadMessageKey, unReadMessage);
        return unReadMessage;
    }

    public void updateUnReadMessage(final String unReadMessageKey) {
        redisTemplate.opsForValue().increment(unReadMessageKey, 1);
    }

    // unReadMessageDirtySet
    public String getUnReadMessageDirtySetKey() {
        return "unReadMessageDirtySetKey";
    }

    public Set<String> getUnReadMessageDirtySet() {
        ScanOptions options = ScanOptions.scanOptions().match("*").count(100).build();
        Cursor<String> cursor = redisTemplate.opsForSet().scan("updatedSetKey", options);

        Set<String> results = new HashSet<>();
        try {
            while (cursor.hasNext()) {
                results.add(cursor.next());
            }
        } finally {
            cursor.close();
        }
        return results;
    }

    public void updateUnReadMessageDirtySet(final Long userId, final Long teamId) {
        redisTemplate.opsForSet().add(getUnReadMessageDirtySetKey(), userId + ":" + teamId);
    }

    public void clearUnReadMessageDirtySet(final Set<String> beforeUpdatedSet) {
        if (beforeUpdatedSet != null && !beforeUpdatedSet.isEmpty()) {
            redisTemplate.opsForSet().remove(getUnReadMessageDirtySetKey(), beforeUpdatedSet.toArray());
        }
    }

    // ZSet
    public String getZSetKey(final Long userId) {
        return "userId:" + userId + ":team";
    }

    public Set<String> getZSet(final String zSetKey) {
        return redisTemplate.opsForZSet().reverseRange(zSetKey, 0, -1);
    }

    public void updateZSet(final String zSetKey, final Long teamId, final Chat chat) {
        long score = chat.getCreatedAt()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli();
        redisTemplate.opsForZSet().add(zSetKey, String.valueOf(teamId), score);
    }

    public void updateZSet(final String zSetKey, final Long teamId, final LocalDateTime lastChatTime) {
        long score = lastChatTime
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli();
        redisTemplate.opsForZSet().add(zSetKey, String.valueOf(teamId), score);
    }

    public void clearZSet(final String zSetKey) {
        redisTemplate.opsForZSet().removeRange(zSetKey, 0, -1);
    }

    // Term FindCount + FindTime
    // Hash의 getAll은 O(N)이기 때문에 key 값으로 호출 날짜 yyyyMMddHH를 사용해 나눠 저장하자.
    public String getTermFindCountHashKey(final LocalDateTime lastFindTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        return lastFindTime.format(dateTimeFormatter);
    }

    public String getTermFindTimeZSetKey() {
        return "termFindTimeZSetKey";
    }

    public void updateTermFindCountHash(final String termFindCountKey, final String word, final Language language,
                                        final Integer count) {
        String field = word + ":" + language.toString();
        redisTemplate.opsForHash().increment(termFindCountKey, field, count);
    }

    public void updateTermFindTimeZSet(final String word, final Language language, final LocalDateTime lastFindTime) {
        long score = lastFindTime
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli();
        redisTemplate.opsForZSet().add(getTermFindTimeZSetKey(), word + ":" + language.toString(), score);
    }

    // 종합
    public void initRedis(final Long userId, final Long teamId) {
        String inRoomKey = getInRoomKey(userId, teamId);
        String unReadMessageKey = getUnReadMessageKey(userId, teamId);
        String zSetKey = getZSetKey(userId);

        initInRoom(inRoomKey);
        initUnReadMessage(unReadMessageKey);
        initZSet(zSetKey, teamId);
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
        String inRoomKey = getInRoomKey(userId, teamId);
        String unReadMessageKey = getUnReadMessageKey(userId, teamId);
        String zSetKey = getZSetKey(userId);
        redisTemplate.delete(inRoomKey);
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

    private void updateInRoom(final String inRoomKey, final int inRoom) {
        redisTemplate.opsForValue().set(inRoomKey, String.valueOf(inRoom));
    }

    private Boolean isUnReadMessageExists(final String unReadMessageKey) {
        return redisTemplate.hasKey(unReadMessageKey);
    }

    private int getUnReadMessage(final String unReadMessageKey) {
        String unReadMessageValue = redisTemplate.opsForValue().get(unReadMessageKey);
        return unReadMessageValue != null ? Integer.parseInt(unReadMessageValue) : 0;
    }

    private void updateUnReadMessage(final String unReadMessageKey, final int unReadMessage) {
        redisTemplate.opsForValue().set(unReadMessageKey, String.valueOf(unReadMessage));
    }

    private void initInRoom(final String inRoomKey) {
        redisTemplate.opsForValue().set(inRoomKey, "0", TTL, TimeUnit.MINUTES);
    }

    private void initUnReadMessage(final String unReadMessageKey) {
        redisTemplate.opsForValue().set(unReadMessageKey, "0", TTL, TimeUnit.MINUTES);
    }

    private void initZSet(final String zSetKey, final Long teamId) {
        long score = LocalDateTime.now()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli();
        redisTemplate.opsForZSet().add(zSetKey, String.valueOf(teamId), score);
        redisTemplate.expire(zSetKey, TTL, TimeUnit.MINUTES);
    }
}
