package org.gdsccau.team5.safebridge.domain.term.scheduler;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.cache.CacheType;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.term.service.TermCacheCheckService;
import org.gdsccau.team5.safebridge.domain.term.service.TermCacheService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotTermScheduler {

    private final RedisManager redisManager;
    private final TermCacheService termCacheService;
    private final TermCacheCheckService termCacheCheckService;

    @Scheduled(fixedRate = 300000)
    public void hotTermUpdate() {
        // 1. Local Cache에 저장된 누적 호출 횟수, 최근 호출 시각을 가져온다.
        Map<Object, Object> cacheEntriesForCount = termCacheCheckService.getAllKeyAndValueInCache(CacheType.TERM_FIND_COUNT.getCacheName());
        Map<Object, Object> cacheEntriesForTime = termCacheCheckService.getAllKeyAndValueInCache(CacheType.TERM_FIND_TIME.getCacheName());

        // 2. 누적 호출 횟수는 Hash를, 최근 호출 시각은 ZSet을 이용해 Redis에 값을 업데이트 한다.


        // 3. Local Cache에 저장했던 값은 삭제한다.

    }
}
