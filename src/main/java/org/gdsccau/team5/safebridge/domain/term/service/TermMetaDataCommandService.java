package org.gdsccau.team5.safebridge.domain.term.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.cache.CacheType;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermMetaDataCommandService {

    private final TermCacheCommandService termCacheCommandService;
    private final TermCacheQueryService termCacheQueryService;

    @Async("threadPoolTaskExecutor")
    public void updateTermMetaDataInLocalCache(final List<TermDataDto> terms, final Set<Language> languageSet,
                                               final LocalDateTime chatTime) {
        terms.forEach(dto -> {
            languageSet.forEach(language -> {
                String word = dto.getTerm();
                termCacheCommandService.updateFindCount(word, language);
                termCacheCommandService.updateFindTime(word, language, chatTime);
            });
        });

//        Map<Object, Object> cacheEntriesForTime = termCacheQueryService.getAllKeyAndValueInCache(
//                CacheType.TERM_FIND_TIME.getCacheName());
//        Map<Object, Object> cacheEntriesForCount = termCacheQueryService.getAllKeyAndValueInCache(
//                CacheType.TERM_FIND_COUNT.getCacheName());
//
//        cacheEntriesForTime.forEach((key, value) -> {
//            System.out.println("key = " + key + " value = " + value);
//        });
//        cacheEntriesForCount.forEach((key, value) -> {
//            System.out.println("key = " + key + " value = " + value);
//        });
    }
}
