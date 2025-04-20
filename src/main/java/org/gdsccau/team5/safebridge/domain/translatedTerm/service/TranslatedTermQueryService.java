package org.gdsccau.team5.safebridge.domain.translatedTerm.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.code.error.TranslatedTermErrorCode;
import org.gdsccau.team5.safebridge.common.exception.handler.ExceptionHandler;
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

    public String findTranslatedWordByLanguageAndTermId(final Language language, final Long termId) {
        String word = translatedTermRepository.findTranslatedWordByLanguageAndTermId(language, termId).orElse(null);
        this.validate(word);
        return word;
    }

    public Boolean existsByLanguageAndTermId(final Language language, final Long termId) {
        Boolean isExist = translatedTermRepository.existsByLanguageAndTermId(language, termId);
        this.validate(isExist);
        return isExist;
    }

    public List<TranslatedWordAndTermIdDto> findTranslatedWordsByLanguageAndTermIds(final Language language,
                                                                                    final List<Long> termIds) {
        List<TranslatedWordAndTermIdDto> dtos = translatedTermRepository.findTranslatedTermsByLanguageAndTermIds(termIds, language);
        this.validate(dtos);
        return dtos;
    }

    public List<TranslatedWordAndLanguageDto> findTranslatedWordsByLanguagesAndTermId(final List<Language> languages,
                                                                                      final Long termId) {
        List<TranslatedWordAndLanguageDto> dtos = translatedTermRepository.findTranslatedTermsByLanguagesAndTermId(termId, languages);
        this.validate(dtos);
        return dtos;
    }

    public <T> void validate(final T data) {
        if (data == null) {
            throw new ExceptionHandler(TranslatedTermErrorCode.TRANSLATED_TERM_NOT_FOUND);
        }
    }

    public <T> void validate(final List<T> data) {
        if (data.isEmpty()) {
            throw new ExceptionHandler(TranslatedTermErrorCode.TRANSLATED_TERM_NOT_FOUND);
        }
    }
}
