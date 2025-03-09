package org.gdsccau.team5.safebridge.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.gdsccau.team5.safebridge.common.term.Language;

public record UserRequestDto(
) {

  public record LoginDto(@NotBlank(message = "로그인 아이디는 필수입니다!") String loginId,
                         @NotBlank(message = "비밀번호 입력은 필수입니다!") String password) {

  }

  public record UserSignUpDto(@NotBlank(message = "이름은 필수입니다.") String name,
                              @NotNull(message = "언어선택은 필수입니다!") Language language,
                              @NotBlank(message = "로그인 아이디는 필수입니다!") String loginId,
                              @NotBlank(message = "비밀번호 입력은 필수입니다!") String password) {

  }

  public record AdminSignUpDto(@NotBlank(message = "이름은 필수입니다.") String name,
                               @NotBlank(message = "로그인 아이디는 필수입니다.") String loginId,
                               @NotBlank(message = "비밀번호 입력은 필수입니다.") String password) {

  }
}
