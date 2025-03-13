package org.gdsccau.team5.safebridge.domain.userAdmin.dto;

import java.util.List;
import lombok.Builder;

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
        String name,
        Boolean isTemporaryWorker
    ) {


    }
  }
}
