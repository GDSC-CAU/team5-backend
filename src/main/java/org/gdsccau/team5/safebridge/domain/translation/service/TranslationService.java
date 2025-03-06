package org.gdsccau.team5.safebridge.domain.translation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatCheckService;
import org.gdsccau.team5.safebridge.domain.translation.TranslationRepository;
import org.gdsccau.team5.safebridge.domain.translation.entity.Translation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TranslationService {

    private final ChatCheckService chatCheckService;
    private final TranslationRepository translationRepository;

    public void createTranslation(final String text, final Language language, final Long chatId) {
        Chat chat = chatCheckService.findChatById(chatId);
        Translation translation = Translation.builder()
                .text(text)
                .language(language)
                .chat(chat)
                .build();
        translationRepository.save(translation);
    }
}
