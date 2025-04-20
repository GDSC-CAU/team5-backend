package org.gdsccau.team5.safebridge.domain.term.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermMetaDataCommandService {

    private final TermCacheCommandService termCacheCommandService;

    @Async
    public void updateTermMetaDataInLocalCache(final List<TermDataDto> terms, final Set<Language> languageSet) {
        log.info("Local Cache에 현장 용어 호출 횟수 저장");
        terms.forEach(dto -> {
            languageSet.forEach(language -> {
                String word = dto.getTerm();
                termCacheCommandService.updateFindCount(word, language);
            });
        });
    }
}
