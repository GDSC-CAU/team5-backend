package org.gdsccau.team5.safebridge.domain.userAdmin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record UserAdminResponseDto() {

  @Builder
  public record CreateDto(Long userId, String name) {
  }
}
