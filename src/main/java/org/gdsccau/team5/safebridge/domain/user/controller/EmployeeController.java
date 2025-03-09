package org.gdsccau.team5.safebridge.domain.user.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.code.success.CommonSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.gdsccau.team5.safebridge.domain.user.dto.EmployeeResponseDto;
import org.gdsccau.team5.safebridge.domain.user.dto.EmployeeResponseDto.ListDto.EmployeeDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserRequestDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserResponseDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserResponseDto.SignUpDto;
import org.gdsccau.team5.safebridge.domain.user.service.EmployeeService;
import org.gdsccau.team5.safebridge.domain.user.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;

  @GetMapping("/admin/{adminId}/employees")
  public ApiResponse<EmployeeResponseDto.ListDto> list(
      @PathVariable("adminId") final Long adminId) {
    EmployeeResponseDto.ListDto listDto = employeeService.getList(adminId);

    return ApiResponse.onSuccess(CommonSuccessCode.OK, listDto);
  }
}
