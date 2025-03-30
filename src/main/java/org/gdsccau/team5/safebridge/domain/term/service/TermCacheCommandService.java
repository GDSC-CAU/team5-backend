package org.gdsccau.team5.safebridge.domain.term.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TermCacheCommandService {

    private final CacheManager cacheManager;

    @CachePut(value = "term", key = "#word + ':' + #language.name()")
    public String updateTerm(final String word, final Language language, final String translatedWord) {
        return translatedWord;
    }

    @CachePut(value = "term", key = "#key")
    public String updateTerm(final String key, final String translatedWord) {
        return translatedWord;
    }

    @CachePut(value = "findCount", key = "#word + ':' + #language.name()")
    public String updateFindCount(final String word, final Language language) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        String currentTime = LocalDateTime.now().format(dateTimeFormatter);
        String currentValue = Objects.requireNonNull(cacheManager.getCache("findCount"))
                .get(word + ":" + language.toString(), String.class);
        int newFindCount = 1;
        if (currentValue != null) {
            newFindCount = Integer.parseInt(currentValue.split(":")[0]) + 1;
        }
        return newFindCount + ":" + currentTime;
    }

    @CachePut(value = "findTime", key = "#word + ':' + #language.name()")
    public String updateFindTime(final String word, final Language language, final LocalDateTime chatTime) {
        return chatTime.toString();
    }

    @CacheEvict(value = "term", key = "#word + ':' + #language.name()")
    public void deleteTerm(final String word, final Language language) {

    }

    @CacheEvict(value = "findCount", key = "#key")
    public void deleteFindCount(final String key) {

    }

    @CacheEvict(value = "findTime", key = "#key")
    public void deleteFindTime(final String key) {

    }
}
