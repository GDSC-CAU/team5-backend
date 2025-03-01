package org.gdsccau.team5.safebridge.domain.chat.dto;

import java.time.LocalDateTime;
import java.util.List;
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
        String lastChat;
        LocalDateTime lastChatTime;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TermDataDto {
        int startIndex;
        int endIndex;
        String term;
        String meaning;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TermDataWithNewChatDto{
        List<TermDataDto> terms;
        String newChat;
    }
}
