package org.gdsccau.team5.safebridge.domain.term.scheduler;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.cache.CacheType;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.common.redis.RedisMessagePublisher;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.term.service.TermCacheCheckService;
import org.gdsccau.team5.safebridge.domain.term.service.TermCacheService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotTermScheduler {

    private final RedisManager redisManager;
    private final RedisMessagePublisher redisMessagePublisher;
    private final TermCacheService termCacheService;
    private final TermCacheCheckService termCacheCheckService;

    @Scheduled(fixedRate = 300000)
    public void hotTermUpdate() {
        // 1. Local Cache에 저장된 누적 호출 횟수, 최근 호출 시각을 가져온다.
        Map<Object, Object> cacheEntriesForTime = termCacheCheckService.getAllKeyAndValueInCache(
                CacheType.TERM_FIND_TIME.getCacheName());
        Map<Object, Object> cacheEntriesForCount = termCacheCheckService.getAllKeyAndValueInCache(
                CacheType.TERM_FIND_COUNT.getCacheName());

        // 2. 누적 호출 횟수는 Hash를, 최근 호출 시각은 ZSet을 이용해 Redis에 값을 업데이트 한다.
        updateTermFindTimeZSet(cacheEntriesForTime);
        updateTermFindCountHash(cacheEntriesForTime, cacheEntriesForCount);

        // 3. Local Cache에 저장했던 값은 삭제한다.
        deleteTermFindTimeInLocalCache(cacheEntriesForTime);
        deleteTermFindCountHashInLocalCache(cacheEntriesForCount);

        // 4. Redis Publish
        redisMessagePublisher.publish("HotTerm", "Ready for Hot Term");
    }

    private void updateTermFindTimeZSet(final Map<Object, Object> cacheEntriesForTime) {
        cacheEntriesForTime.forEach((k, v) -> {
            String word = ((String) k).split(":")[0];
            Language language = Language.valueOf(((String) k).split(":")[1]);
            LocalDateTime chatTime = LocalDateTime.parse((String) v);
            redisManager.updateTermFindTimeZSet(word, language, chatTime);
        });
    }

    private void updateTermFindCountHash(final Map<Object, Object> cacheEntriesForTime,
                                         final Map<Object, Object> cacheEntriesForCount) {
        cacheEntriesForCount.forEach((k, v) -> {
            String word = ((String) k).split(":")[0];
            Language language = Language.valueOf(((String) k).split(":")[1]);
            Integer count = (Integer) v;
            LocalDateTime chatTime = LocalDateTime.parse((String) cacheEntriesForTime.get(k));
            redisManager.updateTermFindCountHash(word, language, count, chatTime);
        });
    }

    private void deleteTermFindTimeInLocalCache(final Map<Object, Object> cacheEntriesForTime) {
        cacheEntriesForTime.forEach((k, v) -> {
            String word = ((String) k).split(":")[0];
            Language language = Language.valueOf(((String) k).split(":")[1]);
            termCacheService.deleteFindTime(word, language);
        });
    }

    private void deleteTermFindCountHashInLocalCache(final Map<Object, Object> cacheEntriesForCount) {
        cacheEntriesForCount.forEach((k, v) -> {
            String word = ((String) k).split(":")[0];
            Language language = Language.valueOf(((String) k).split(":")[1]);
            termCacheService.deleteFindCount(word, language);
        });
    }
}
