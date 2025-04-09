package org.gdsccau.team5.safebridge.domain.translatedTerm.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.translatedTerm.dto.TranslatedTermDto.TranslatedWordAndLanguageDto;
import org.gdsccau.team5.safebridge.domain.translatedTerm.dto.TranslatedTermDto.TranslatedWordAndTermIdDto;
import org.gdsccau.team5.safebridge.domain.translatedTerm.repository.TranslatedTermRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslatedTermQueryService {

    private final TranslatedTermRepository translatedTermRepository;

    @Transactional(readOnly = true)
    public String findTranslatedWordByLanguageAndTermId(final Language language, final Long termId) {
        return translatedTermRepository.findTranslatedWordByLanguageAndTermId(language, termId).orElse(null);
    }

    @Transactional(readOnly = true)
    public Boolean existsByLanguageAndTermId(final Language language, final Long termId) {
        return translatedTermRepository.existsByLanguageAndTermId(language, termId);
    }

    @Transactional(readOnly = true)
    public List<TranslatedWordAndTermIdDto> findTranslatedWordsByLanguageAndTermIds(final Language language,
                                                                                    final List<Long> termIds) {
        return translatedTermRepository.findTranslatedTermsByLanguageAndTermIds(language, termIds);
    }

    @Transactional(readOnly = true)
    public List<TranslatedWordAndLanguageDto> findTranslatedWordsByLanguagesAndTermId(final List<Language> languages,
                                                                                      final Long termId) {
        return translatedTermRepository.findTranslatedTermsByLanguagesAndTermId(languages, termId);
    }
}
