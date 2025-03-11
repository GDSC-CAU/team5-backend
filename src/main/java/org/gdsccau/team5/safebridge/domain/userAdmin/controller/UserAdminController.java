package org.gdsccau.team5.safebridge.domain.userAdmin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.code.success.CommonSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.gdsccau.team5.safebridge.domain.userAdmin.dto.EmployeeResponseDto;
import org.gdsccau.team5.safebridge.domain.userAdmin.dto.UserAdminRequestDto;
import org.gdsccau.team5.safebridge.domain.userAdmin.dto.UserAdminResponseDto;
import org.gdsccau.team5.safebridge.domain.userAdmin.service.UserAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAdminController {

  private final UserAdminService userAdminService;

  @PostMapping("/admin/{adminId}/employees")
  public final ApiResponse<UserAdminResponseDto.CreateDto> createUserAdmin(
      @PathVariable(name = "adminId") Long adminId, @Valid @RequestBody
  UserAdminRequestDto.CreateDto createDto) {
    UserAdminResponseDto.CreateDto responseCreateDto = userAdminService.createUserAdmin(adminId,
        createDto);
    return ApiResponse.onSuccess(CommonSuccessCode.OK, responseCreateDto);
  }

  @GetMapping("/admin/{adminId}/employees")
  public ApiResponse<EmployeeResponseDto.ListDto> list(
      @PathVariable("adminId") final Long adminId) {
    EmployeeResponseDto.ListDto listDto = userAdminService.getList(adminId);

    return ApiResponse.onSuccess(CommonSuccessCode.OK, listDto);
  }
}
