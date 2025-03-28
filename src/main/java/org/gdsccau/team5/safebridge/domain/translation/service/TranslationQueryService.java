package org.gdsccau.team5.safebridge.domain.translation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.translation.TranslationRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TranslationQueryService {

    private final TranslationRepository translationRepository;

    public boolean isTranslationExists(final Long chatId, final Language language) {
        return translationRepository.existsByChatIdAndLanguage(chatId, language);
    }

    public String findTranslatedTextByChatIdAndLanguage(final Long chatId, final Language language) {
        return translationRepository.findTranslatedTextByChatIdAndLanguage(chatId, language).orElse(null);
    }
}
