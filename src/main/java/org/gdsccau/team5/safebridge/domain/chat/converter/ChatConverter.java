package org.gdsccau.team5.safebridge.domain.chat.converter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.request.ChatRequestDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.TranslatedTextResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.user.entity.User;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatConverter {

    public static ChatResponseDto.ChatMessageResponseDto toChatResponseDto(
            final String name, final ChatDto.ChatDetailDto chatDetailDto, final List<TermDataDto> terms) {
        return ChatResponseDto.ChatMessageResponseDto.builder()
                .chatId(chatDetailDto.getChatId())
                .userId(chatDetailDto.getUserId())
                .name(name)
                .message(chatDetailDto.getText())
                .sendTime(chatDetailDto.getCreatedAt())
                .terms(terms)
                .build();
    }

    public static TranslatedTextResponseDto toTranslatedTextResponseDto(
            final String translatedText, final Map<String, String> translatedTerms, final Long chatId) {
        return TranslatedTextResponseDto.builder()
                .translatedText(translatedText)
                .translatedTerms(translatedTerms)
                .chatId(chatId)
                .build();
    }

    public static Chat toChat(final ChatRequestDto.ChatMessageRequestDto chatRequestDto,
                              final User user,
                              final Team team) {
        return Chat.builder()
                .text(chatRequestDto.getMessage())
                .isTodo(chatRequestDto.getIsTodo())
                .user(user)
                .team(team)
                .build();
    }

    public static ChatDto.ChatDetailDto toChatDetailDto(final Chat chat) {
        return ChatDto.ChatDetailDto.builder()
                .chatId(chat.getId())
                .userId(chat.getUser().getId())
                .text(chat.getText())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
