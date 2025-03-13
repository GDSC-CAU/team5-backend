package org.gdsccau.team5.safebridge.domain.chat.converter;

import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
      final String name, final Chat chat, final List<TermDataDto> terms) {
    return ChatResponseDto.ChatMessageResponseDto.builder()
        .chatId(chat.getId())
        .userId(chat.getUser().getId())
        .name(name)
        .message(chat.getText())
        .sendTime(chat.getCreatedAt())
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
        .isTodo(chatRequestDto.isTodo())
        .user(user)
        .team(team)
        .build();
  }
}
