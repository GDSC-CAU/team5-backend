package org.gdsccau.team5.safebridge.domain.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class ChatRequestDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageRequestDto {
        //    String token;
        //    boolean isTodo;
        Long userId;
        String name;
        String message;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastChatIdRequestDto {
        Long lastChatId;
    }
}