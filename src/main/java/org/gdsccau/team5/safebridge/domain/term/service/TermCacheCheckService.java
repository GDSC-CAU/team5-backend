package org.gdsccau.team5.safebridge.domain.term.service;

import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class TermCacheCheckService {

    private final CacheManager cacheManager;

    @Cacheable(value = "term", key = "#word + ':' + #language.toString()")
    public String findTerm(final String word, final Language language) {
        return Objects.requireNonNull(cacheManager.getCache("term")).get(word + ':' + language.toString(), String.class);
    }

    @Cacheable(value = "findNumber", key = "#word + ':' + #language.toString()")
    public Integer findNumber(final String word, final Language language) {
        return Objects.requireNonNull(cacheManager.getCache("findNumber")).get(word + ':' + language.toString(), Integer.class);
    }

    @Cacheable(value = "findTime", key = "#word + ':' + #language.toString()")
    public Long findTime(final String word, final Language language) {
        return Objects.requireNonNull(cacheManager.getCache("findTime")).get(word + ':' + language.toString(), Long.class);
    }
}
