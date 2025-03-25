package org.gdsccau.team5.safebridge.domain.term.service;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TermCacheService {

    private final CacheManager cacheManager;

    @CachePut(value = "term", key = "#word + ':' + #language.toString()")
    public String updateTerm(final String word, final Language language, final String translatedWord) {
        return word + ":" + translatedWord;
    }

    @CachePut(value = "findCount", key = "#word + ':' + #language.toString()")
    public Integer updateFindCount(final String word, final Language language) {
        Integer currentFindCount = Objects.requireNonNull(cacheManager.getCache("findCount"))
                .get(word + ':' + language.toString(), Integer.class);
        return currentFindCount == null ? 1 : currentFindCount + 1;
    }

    @CachePut(value = "findTime", key = "#word + ':' + #language.toString()")
    public String updateFindTime(final String word, final Language language, final LocalDateTime chatTime) {
        return chatTime.toString();
    }

    @CacheEvict(value = "term", key = "#word + ':' + #language.toString()")
    public void deleteTerm(final String word, final Language language) {

    }

    @CacheEvict(value = "findCount", key = "#word + ':' + #language.toString()")
    public void deleteFindCount(final String word, final Language language) {

    }

    @CacheEvict(value = "findTime", key = "#word + ':' + #language.toString()")
    public void deleteFindTime(final String word, final Language language) {

    }
}
