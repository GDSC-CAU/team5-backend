package org.gdsccau.team5.safebridge.domain.term.dto;

import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gdsccau.team5.safebridge.common.term.Language;

public class TermDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermIdAndWordDto {
        Long termId;
        String word;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTranslatedTermEntityDto {
        String word;
        Language language;
        String translatedWord;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermPairDto {
        Map<String, String> translatedTerms;
        Set<CreateTranslatedTermEntityDto> ttSet;
    }
}
