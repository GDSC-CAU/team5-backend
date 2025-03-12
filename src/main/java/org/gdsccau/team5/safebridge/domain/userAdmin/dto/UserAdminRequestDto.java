package org.gdsccau.team5.safebridge.domain.userAdmin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public record UserAdminRequestDto() {

  public record CreateDto(@NotNull(message = "근로자 식별자 아이디는 필수입니다.") Long userId,
                          boolean isTemporaryWorker) {
  }

  public record DeleteDto(@NotNull(message = "근로자 식별자 아이디는 필수입니다.") Long userId,
                          boolean isDeleteAllTemporaryWorker) {

  }
}
