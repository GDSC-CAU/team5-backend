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
    public static class DecideToCreateTermEntityDto {
        String word;
        String meaning;
        Language language;
        String translatedWord;
        int choice; // 1이면 tt만 저장, 2면 t랑 tt 둘다 저장
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermPairDto {
        Map<String, String> translatedTerms;
        Set<DecideToCreateTermEntityDto> decidableSet;
    }
}
