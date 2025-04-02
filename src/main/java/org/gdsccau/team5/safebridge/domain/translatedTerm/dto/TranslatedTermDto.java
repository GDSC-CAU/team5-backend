package org.gdsccau.team5.safebridge.domain.translatedTerm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.term.entity.Term;

public class TranslatedTermDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslatedWordAndTermIdDto {
        String translatedWord;
        Long termId;
    }
}
