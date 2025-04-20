package org.gdsccau.team5.safebridge.domain.translatedTerm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;
import org.gdsccau.team5.safebridge.domain.translatedTerm.repository.TranslatedTermRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslatedTermCommandService {

    private final TranslatedTermRepository translatedTermRepository;

    public void createTranslatedTerm(final Long termId, final Language language, final String word) {
        translatedTermRepository.upsertTranslatedTerm(language.name(), word, termId);
    }
}
