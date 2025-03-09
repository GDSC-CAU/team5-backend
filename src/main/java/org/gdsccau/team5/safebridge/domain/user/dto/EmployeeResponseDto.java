package org.gdsccau.team5.safebridge.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.user.entity.User;

@Builder
public record EmployeeResponseDto(
) {
  @Builder
  public record ListDto(
      Long adminId,
      List<EmployeeDto> employees,
      Integer employeeNumber
  ) {
    @Builder
    public record EmployeeDto(
        Long userId,
        String name
    ) {


    }
  }
}
