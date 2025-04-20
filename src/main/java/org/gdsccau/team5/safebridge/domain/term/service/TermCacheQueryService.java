package org.gdsccau.team5.safebridge.domain.term.service;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TermCacheQueryService {

    private final CacheManager cacheManager;

    @Cacheable(value = "term", key = "#word + ':' + #language.name()")
    public String findTerm(final String word, final Language language) {
        return null;
    }

    public Map<Object, Object> getAllKeyAndValueInCache(final String cacheName) {
        CaffeineCacheManager caffeineCacheManager = (CaffeineCacheManager) cacheManager;
        CaffeineCache cache = (CaffeineCache) caffeineCacheManager.getCache(cacheName);
        Cache<Object, Object> caffeineCache = cache.getNativeCache();
        return new HashMap<>(caffeineCache.asMap());
    }
}
