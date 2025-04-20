package org.gdsccau.team5.safebridge.domain.chat.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gdsccau.team5.safebridge.domain.term.dto.TermDto;

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
    public static class ChatDetailDto {
        Long chatId;
        Long userId;
        String text;
        LocalDateTime createdAt;
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
    public static class TermDataWithNewChatDto {
        List<TermDataDto> terms;
        String newChat;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TranslatedDataDto {
        String translatedText;
        Map<String, String> translatedTerms;
        Set<TermDto.CreateTranslatedTermEntityDto> ttSet;
    }
}
