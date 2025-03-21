package org.gdsccau.team5.safebridge.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();

        List<CaffeineCache> caches =
                Arrays.stream(CacheType.values())
                        .map(cache -> new CaffeineCache(
                                cache.getCacheName(),
                                Caffeine.newBuilder()
                                        .expireAfterWrite(cache.getExpiredAfterWrite(), TimeUnit.MINUTES)
                                        .maximumSize(cache.getMaximumSize())
                                        .recordStats()
                                        .build()
                        )).toList();
        simpleCacheManager.setCaches(caches);
        return simpleCacheManager;
    }
}
