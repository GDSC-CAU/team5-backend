package org.gdsccau.team5.safebridge.domain.chat.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class ChatResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageResponseDto {
        String name;
        String message;
        LocalDateTime sendTime;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageWithIsReadResponseDto {
        Long chatId;
        String name;
        String message;
        boolean isRead;
        LocalDateTime sendTime;

        public void setRead(boolean isRead) {
            this.isRead = isRead;
        }
    }
}
