package org.gdsccau.team5.safebridge.domain.chat.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMetaDataDto {
        String teamName;
        String lastChat;
        LocalDateTime lastChatTime;
    }
}
