package org.gdsccau.team5.safebridge.domain.term.service;

import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TermCacheService {

    private final CacheManager cacheManager;

    @CachePut(value = "findNumber", key = "#termId + #language.toString()")
    public Integer updateFindNumber(final Long termId, final Language language) {
        Integer currentFindNumber = Objects.requireNonNull(cacheManager.getCache("findNumber")).get(termId + language.toString(), Integer.class);
        return currentFindNumber == null ? 1 : currentFindNumber + 1;
    }

    @CachePut(value = "findTime", key = "#termId + #language.toString()")
    public Long updateFindTime(final Long termId, final Language language) {
        return LocalDateTime.now()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli();
    }

    @CacheEvict(value = "findNumber", key = "#termId + #language.toString()")
    public void deleteFindNumber(final Long termId, final Language language) {

    }

    @CacheEvict(value = "findTime", key = "#termId + #language.toString()")
    public void deleteFindTime(final Long termId, final Language language) {

    }
}
