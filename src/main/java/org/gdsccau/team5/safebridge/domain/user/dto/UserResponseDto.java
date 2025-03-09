package org.gdsccau.team5.safebridge.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record UserResponseDto() {
  public record LoginDto(Long id, String token) {}
}
