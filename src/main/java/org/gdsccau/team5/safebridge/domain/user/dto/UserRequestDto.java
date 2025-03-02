package org.gdsccau.team5.safebridge.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gdsccau.team5.safebridge.common.term.Language;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserRequestDto {
    private String name;
    private Language language;
}
