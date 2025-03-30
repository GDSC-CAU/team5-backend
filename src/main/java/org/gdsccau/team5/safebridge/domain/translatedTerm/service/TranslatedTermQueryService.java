package org.gdsccau.team5.safebridge.domain.translatedTerm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.translatedTerm.repository.TranslatedTermRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslatedTermQueryService {

    private final TranslatedTermRepository translatedTermRepository;

    public String findTranslatedWordByLanguageAndTermId(final Language language, final Long termId) {
        return translatedTermRepository.findTranslatedWordByLanguageAndTermId(language, termId).orElse(null);
    }

    public Boolean isTranslatedTermExists(final Language language, final Long termId) {
        return translatedTermRepository.existsByLanguageAndTermId(language, termId);
    }
}
