package org.gdsccau.team5.safebridge.domain.user.dto;

import org.gdsccau.team5.safebridge.domain.user.enums.Role;

public record UserResponseDto() {
  public record LoginDto(Long id, Role role, String token) {}
  public record SignUpDto(Long id, Role role, String token) {}
}
