package org.gdsccau.team5.safebridge.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.user.entity.User;

@Builder
public record UserRequestDto(
) {

  public record LoginDto(String loginId, String password) {

  }

  public record UserSignUpDto(String name, Language language, String loginId,
                              String password) {

  }

  public record AdminSignUpDto(String name, String loginId,
                               String password) {

  }
}
