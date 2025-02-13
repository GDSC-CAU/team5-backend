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
        String name;
        String message;
    }
}