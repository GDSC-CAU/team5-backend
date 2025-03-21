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

    @CachePut(value = "term", key = "#word + ':' + #language.toString()")
    public String updateTerm(final String word, final Language language, final String translatedWord) {
        return word + ":" + translatedWord;
    }

    @CachePut(value = "findNumber", key = "#word + ':' + #language.toString()")
    public Integer updateFindNumber(final String word, final Language language) {
        Integer currentFindNumber = Objects.requireNonNull(cacheManager.getCache("findNumber")).get(word + ':' + language.toString(), Integer.class);
        return currentFindNumber == null ? 1 : currentFindNumber + 1;
    }

    @CachePut(value = "findTime", key = "#word + ':' + #language.toString()")
    public Long updateFindTime(final String word, final Language language, final LocalDateTime chatTime) {
        return chatTime.atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli();
    }

    @CacheEvict(value = "term", key = "#word + ':' + #language.toString()")
    public void deleteTerm(final String word, final Language language) {

    }

    @CacheEvict(value = "findNumber", key = "#word + ':' + #language.toString()")
    public void deleteFindNumber(final String word, final Language language) {

    }

    @CacheEvict(value = "findTime", key = "#word + ':' + #language.toString()")
    public void deleteFindTime(final String word, final Language language) {

    }
}
