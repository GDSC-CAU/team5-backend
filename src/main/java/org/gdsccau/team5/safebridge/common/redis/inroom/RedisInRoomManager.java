package org.gdsccau.team5.safebridge.common.redis.inroom;


import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.util.RedisUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisInRoomManager {

    private final RedisTemplate<String, String> redisTemplate;

    public void initInRoom(final String inRoomKey) {
        redisTemplate.opsForValue().set(inRoomKey, "0", RedisUtil.TTL, TimeUnit.MINUTES);
    }

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

    public void updateInRoomWhenJoin(final String inRoomKey) {
        redisTemplate.opsForValue().set(inRoomKey, "1");
    }

    public void updateInRoomWhenLeave(final String inRoomKey) {
        redisTemplate.opsForValue().set(inRoomKey, "0");
    }

    public void deleteInRoom(final String inRoomKey) {
        redisTemplate.delete(inRoomKey);
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
}
