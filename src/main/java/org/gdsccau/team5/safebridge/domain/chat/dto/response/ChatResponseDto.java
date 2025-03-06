package org.gdsccau.team5.safebridge.domain.chat.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataDto;

public class ChatResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageResponseDto {
        Long chatId;
        String name;
        String message;
        LocalDateTime sendTime;
        List<TermDataDto> terms;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageWithIsReadResponseDto {
        Long chatId;
        String name;
        String message;
        String translatedMessage;
        boolean isRead;
        LocalDateTime sendTime;

        public void setRead(boolean isRead) {
            this.isRead = isRead;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslatedTextResponseDto {
        Long chatId;
        String translatedText;
    }
}
