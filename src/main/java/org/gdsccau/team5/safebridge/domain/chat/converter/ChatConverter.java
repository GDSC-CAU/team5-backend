package org.gdsccau.team5.safebridge.domain.chat.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gdsccau.team5.safebridge.domain.chat.dto.request.ChatRequestDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.user.entity.User;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatConverter {

    public static ChatResponseDto.ChatMessageResponseDto toChatResponseDto(
            final String name, final Chat chat) {
        return ChatResponseDto.ChatMessageResponseDto.builder()
                .name(name)
                .message(chat.getText())
                .sendTime(chat.getCreatedAt())
                .build();
    }

    public static Chat toChat(final ChatRequestDto.ChatMessageRequestDto chatRequestDto, final User user,
                              final Team team) {
        return Chat.builder()
                .text(chatRequestDto.getMessage())
                .isTodo(false)
                .user(user)
                .team(team)
                .build();
    }
}
