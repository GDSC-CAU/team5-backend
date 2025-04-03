package org.gdsccau.team5.safebridge.domain.term.scheduler;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.cache.CacheType;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.common.redis.publisher.RedisMessagePublisher;
import org.gdsccau.team5.safebridge.domain.term.service.TermCacheQueryService;
import org.gdsccau.team5.safebridge.domain.term.service.TermCacheCommandService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotTermScheduler {

    private final RedisManager redisManager;
    private final RedisMessagePublisher redisMessagePublisher;
    private final TermCacheCommandService termCacheCommandService;
    private final TermCacheQueryService termCacheQueryService;

    @Scheduled(fixedRate = 36000000) // 1시간
    public void hotTermUpdate() {
        // 1. Local Cache에 저장된 누적 호출 횟수, 최근 호출 시각을 가져온다.
        Map<Object, Object> cacheEntriesForTime = termCacheQueryService.getAllKeyAndValueInCache(
                CacheType.TERM_FIND_TIME.getCacheName());
        Map<Object, Object> cacheEntriesForCount = termCacheQueryService.getAllKeyAndValueInCache(
                CacheType.TERM_FIND_COUNT.getCacheName());

        // 2. 누적 호출 횟수는 Hash를, 최근 호출 시각은 ZSet을 이용해 Redis에 값을 업데이트 한다.
        updateTermFindTimeZSet(cacheEntriesForTime);
        updateTermFindCountHash(cacheEntriesForCount);

        // 3. Local Cache에 저장했던 값은 삭제한다.
        deleteTermFindTimeInLocalCache(cacheEntriesForTime);
        deleteTermFindCountHashInLocalCache(cacheEntriesForCount);

        // 4. Redis Publish
        redisMessagePublisher.publish("HotTerm", "Ready for Hot Term");
    }

    private void updateTermFindTimeZSet(final Map<Object, Object> cacheEntriesForTime) {
        cacheEntriesForTime.forEach((k, v) -> {
            String member = (String) k;
            LocalDateTime chatTime = LocalDateTime.parse((String) v);
            redisManager.updateTermFindTimeZSet(member, chatTime);
        });
    }

    private void updateTermFindCountHash(final Map<Object, Object> cacheEntriesForCount) {
        cacheEntriesForCount.forEach((k, v) -> {
            String field = (String) k;
            Integer count = Integer.parseInt(((String) v).split(":")[0]);
            String findCountHashKey = ((String) v).split(":")[1];
            redisManager.updateTermFindCountHash(field, count, findCountHashKey);
        });
    }

    private void deleteTermFindTimeInLocalCache(final Map<Object, Object> cacheEntriesForTime) {
        cacheEntriesForTime.forEach((k, v) -> {
            String key = (String) k;
            termCacheCommandService.deleteFindTime(key);
        });
    }

    private void deleteTermFindCountHashInLocalCache(final Map<Object, Object> cacheEntriesForCount) {
        cacheEntriesForCount.forEach((k, v) -> {
            String key = (String) k;
            termCacheCommandService.deleteFindCount(key);
        });
    }
}
