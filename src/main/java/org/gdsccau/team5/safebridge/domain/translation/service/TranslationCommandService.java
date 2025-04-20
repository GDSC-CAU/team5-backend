package org.gdsccau.team5.safebridge.domain.translation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatQueryService;
import org.gdsccau.team5.safebridge.domain.translation.repository.TranslationRepository;
import org.gdsccau.team5.safebridge.domain.translation.entity.Translation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TranslationCommandService {

    private final ChatQueryService chatQueryService;
    private final TranslationRepository translationRepository;

    @Transactional
    public void createTranslation(final String text, final Language language, final Long chatId) {
        Chat chat = chatQueryService.findChatById(chatId);
        Translation translation = Translation.builder()
                .text(text)
                .language(language)
                .chat(chat)
                .build();
        translationRepository.save(translation);
    }
}
