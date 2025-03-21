package org.gdsccau.team5.safebridge.common.config;

import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CachingProvider cachingProvider = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
        javax.cache.CacheManager jCacheManager = cachingProvider.getCacheManager();
        javax.cache.configuration.Configuration<Object, Object> configuration = new MutableConfiguration<>()
                .setTypes(Object.class, Object.class)
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.FIVE_MINUTES))
                .setStoreByValue(false)
                .setStatisticsEnabled(true);

        jCacheManager.createCache("localCache", configuration);
        return new JCacheCacheManager(jCacheManager);
    }
}
