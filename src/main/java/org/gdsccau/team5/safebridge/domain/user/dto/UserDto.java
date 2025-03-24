package org.gdsccau.team5.safebridge.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gdsccau.team5.safebridge.common.term.Language;

public class UserDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserIdAndLanguageDto {
        private Long userId;
        private Language language;
    }
}
