package org.gdsccau.team5.safebridge.domain.term.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataDto;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermMetaDataService {

    private final TermCacheService termCacheService;

    @Async("threadPoolTaskExecutor")
    public void updateTermMetaDataInLocalCache(final List<TermDataDto> terms, final Set<Language> languageSet,
                                               final LocalDateTime chatTime) {
        terms.forEach(dto -> {
            languageSet.forEach(language -> {
                String word = dto.getTerm();
                termCacheService.updateFindCount(word, language);
                termCacheService.updateFindTime(word, language, chatTime);
            });
        });
    }
}
