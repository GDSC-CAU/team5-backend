package org.gdsccau.team5.safebridge.common.redis;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.inroom.RedisInRoomManager;
import org.gdsccau.team5.safebridge.common.redis.teamlist.RedisTeamListManager;
import org.gdsccau.team5.safebridge.common.redis.term.RedisTermManager;
import org.gdsccau.team5.safebridge.common.redis.unreadmessage.RedisUrmManager;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisManager {

    private final RedisInRoomManager redisInRoomManager;
    private final RedisUrmManager redisUrmManager;
    private final RedisTeamListManager redisTeamListManager;
    private final RedisTermManager redisTermManager;

    public void initRedis(final Long userId, final Long teamId) {
        String inRoomKey = redisInRoomManager.getInRoomKey(userId, teamId);
        String unReadMessageKey = redisUrmManager.getUnReadMessageKey(userId, teamId);
        String teamListKey = redisTeamListManager.getTeamListKey(userId);

        redisInRoomManager.initInRoom(inRoomKey);
        redisUrmManager.initUnReadMessage(unReadMessageKey);
        redisTeamListManager.initTeamList(teamListKey, teamId);
    }

    public String getInRoomKey(final Long userId, final Long teamId) {
        return redisInRoomManager.getInRoomKey(userId, teamId);
    }

    public String getUnReadMessageKey(final Long userId, final Long teamId) {
        return redisUrmManager.getUnReadMessageKey(userId, teamId);
    }

    public String getTeamListKey(final Long userId) {
        return redisTeamListManager.getTeamListKey(userId);
    }

    public int getInRoomOrDefault(final String inRoomKey, final Supplier<Integer> dbLookUp) {
        return redisInRoomManager.getInRoomOrDefault(inRoomKey, dbLookUp);
    }

    public int getUnReadMessageOrDefault(final String unReadMessageKey, final Supplier<Integer> dbLookUp) {
        return redisUrmManager.getUnReadMessageOrDefault(unReadMessageKey, dbLookUp);
    }

    public Set<String> getUnReadMessageDirtySet() {
        return redisUrmManager.getUnReadMessageDirtySet();
    }

    public Set<String> getTeamList(final String teamListKey) {
        return redisTeamListManager.getTeamList(teamListKey);
    }

    public Map<Object, Object> getTermFindCount(final LocalDateTime chatTime) {
        return redisTermManager.getTermFindCount(chatTime);
    }

    public void updateUnReadMessage(final String unReadMessageKey) {
        redisUrmManager.updateUnReadMessage(unReadMessageKey);
    }

    public void updateUnReadMessageDirtySet(final Long userId, final Long teamId) {
        redisUrmManager.updateUnReadMessageDirtySet(userId, teamId);
    }

    public void updateTeamList(final String teamListKey, final Long teamId, final Chat chat) {
        redisTeamListManager.updateTeamList(teamListKey, teamId, chat);
    }

    // TODO Warming 할 때 TTL 갱신을 해야할까? + TTL과 Warming 주기의 관계에 대해서 고민!
    public void updateTeamList(final String teamListKey, final Long teamId, final LocalDateTime lastChatTime) {
        redisTeamListManager.updateTeamList(teamListKey, teamId, lastChatTime);
    }

    public void updateTermFindCountHash(final String field, final Integer count, final String findCountHashKey) {
        redisTermManager.updateTermFindCountHash(field, count, findCountHashKey);
    }

    public void updateRedisWhenJoin(final Long userId, final Long teamId) {
        String inRoomKey = redisInRoomManager.getInRoomKey(userId, teamId);
        String unReadMessageKey = redisUrmManager.getUnReadMessageKey(userId, teamId);
        redisInRoomManager.updateInRoomWhenJoin(inRoomKey);
        redisUrmManager.updateUnReadMessageWhenJoinAndLeave(unReadMessageKey);
    }

    public void updateRedisWhenLeave(final Long userId, final Long teamId) {
        String inRoomKey = redisInRoomManager.getInRoomKey(userId, teamId);
        String unReadMessageKey = redisUrmManager.getUnReadMessageKey(userId, teamId);
        redisInRoomManager.updateInRoomWhenLeave(inRoomKey);
        redisUrmManager.updateUnReadMessageWhenJoinAndLeave(unReadMessageKey);
    }

    public void updateRedisWhenDelete(final Long userId, final Long teamId) {
        String inRoomKey = redisInRoomManager.getInRoomKey(userId, teamId);
        String unReadMessageKey = redisUrmManager.getUnReadMessageKey(userId, teamId);
        String teamListKey = redisTeamListManager.getTeamListKey(userId);
        redisInRoomManager.deleteInRoom(inRoomKey);
        redisUrmManager.deleteUnReadMessage(unReadMessageKey);
        redisTeamListManager.deleteTeamList(teamListKey, teamId);
    }

    public void clearUnReadMessageDirtySet(final Set<String> beforeUpdatedSet) {
        redisUrmManager.clearUnReadMessageDirtySet(beforeUpdatedSet);
    }

    public void clearTeamList(final String teamListKey) {
        redisTeamListManager.clearTeamList(teamListKey);
    }
}
