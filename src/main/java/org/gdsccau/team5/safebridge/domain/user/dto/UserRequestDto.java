package org.gdsccau.team5.safebridge.domain.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.user.entity.User;

@Builder
public record UserRequestDto(
) {

  public record LoginDto(@NotBlank(message = "로그인 아이디는 필수입니다!") String loginId,
                         @NotBlank(message = "비밀번호 입력은 필수입니다!") String password) {

  }

  public record UserSignUpDto(@NotBlank(message = "이름은 필수입니다.") String name,
                              @NotBlank(message = "언어선택은 필수입니다!") Language language,
                              @NotBlank(message = "로그인 아이디는 필수입니다!") String loginId,
                              @NotBlank(message = "비밀번호 입력은 필수입니다!") String password) {

  }

  public record AdminSignUpDto(@NotBlank(message = "이름은 필수입니다.") String name,
                               @NotBlank(message = "로그인 아이디는 필수입니다.") String loginId,
                               @NotBlank(message = "비밀번호 입력은 필수입니다.") String password) {

  }
}
