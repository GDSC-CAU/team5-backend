package org.gdsccau.team5.safebridge.domain.translatedTerm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;
import org.gdsccau.team5.safebridge.domain.translatedTerm.entity.TranslatedTerm;
import org.gdsccau.team5.safebridge.domain.translatedTerm.repository.TranslatedTermRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslatedTermService {

    private final TranslatedTermRepository translatedTermRepository;

    @Transactional
    public void createTranslatedTerm(final Term term, final Language language, final String word) {
        TranslatedTerm translatedTerm = TranslatedTerm.builder()
                .language(language)
                .word(word)
                .term(term)
                .build();
        translatedTermRepository.save(translatedTerm);
    }
}
