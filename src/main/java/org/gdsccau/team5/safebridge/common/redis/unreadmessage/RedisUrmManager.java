package org.gdsccau.team5.safebridge.common.redis.unreadmessage;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.util.RedisUtil;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUrmManager {

    private final RedisTemplate<String, String> redisTemplate;

    public void initUnReadMessage(final String unReadMessageKey) {
        redisTemplate.opsForValue().set(unReadMessageKey, "0", RedisUtil.TTL, TimeUnit.MINUTES);
    }

    public String getUnReadMessageKey(final Long userId, final Long teamId) {
        return "userId:" + userId + ":teamId:" + teamId + ":unReadMessage";
    }

    public String getUnReadMessageDirtySetKey() {
        return "unReadMessageDirtySetKey";
    }

    public int getUnReadMessageOrDefault(final String unReadMessageKey, final Supplier<Integer> dbLookUp) {
        if (isUnReadMessageExists(unReadMessageKey)) {
            return getUnReadMessage(unReadMessageKey);
        }
        int unReadMessage = dbLookUp.get();
        updateUnReadMessage(unReadMessageKey, unReadMessage);
        return unReadMessage;
    }

    public Set<String> getUnReadMessageDirtySet() {
        ScanOptions options = ScanOptions.scanOptions().match("*").count(100).build();
        Cursor<String> cursor = redisTemplate.opsForSet().scan(getUnReadMessageDirtySetKey(), options);

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

    public void updateUnReadMessage(final String unReadMessageKey) {
        redisTemplate.opsForValue().increment(unReadMessageKey, 1);
    }

    public void updateUnReadMessageWhenJoinAndLeave(final String unReadMessageKey) {
        redisTemplate.opsForValue().set(unReadMessageKey, "0");
    }

    public void updateUnReadMessageDirtySet(final Long userId, final Long teamId) {
        redisTemplate.opsForSet().add(getUnReadMessageDirtySetKey(), userId + ":" + teamId);
    }

    public void clearUnReadMessageDirtySet(final Set<String> beforeUpdatedSet) {
        if (beforeUpdatedSet != null && !beforeUpdatedSet.isEmpty()) {
            redisTemplate.opsForSet().remove(getUnReadMessageDirtySetKey(), beforeUpdatedSet.toArray());
        }
    }

    public void deleteUnReadMessage(final String unReadMessage) {
        redisTemplate.delete(unReadMessage);
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
}
