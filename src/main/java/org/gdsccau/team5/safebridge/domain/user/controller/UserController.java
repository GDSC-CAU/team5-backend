package org.gdsccau.team5.safebridge.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.code.success.CommonSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.gdsccau.team5.safebridge.domain.user.dto.UserRequestDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserResponseDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserResponseDto.SignUpDto;
import org.gdsccau.team5.safebridge.domain.user.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  @Autowired
  UserAuthService userAuthService;

  @PostMapping("/auth/login")
  public ApiResponse<UserResponseDto.LoginDto> login(
      @RequestBody @Valid final UserRequestDto.LoginDto loginDto) {
    UserResponseDto.LoginDto loginResponseDto = userAuthService.login(loginDto);

    return ApiResponse.onSuccess(CommonSuccessCode.OK, loginResponseDto);
  }

  @PostMapping("/auth/user/signup")
  public ApiResponse<UserResponseDto.SignUpDto> signUp(
      @RequestBody @Valid final UserRequestDto.UserSignUpDto userSignUpDto) {

    SignUpDto signUpResponseDto = userAuthService.signUpUser(userSignUpDto);

    return ApiResponse.onSuccess(CommonSuccessCode.OK, signUpResponseDto);
  }

  @PostMapping("/auth/admin/signup")
  public ApiResponse<SignUpDto> adminSignUp(
      @RequestBody @Valid final UserRequestDto.AdminSignUpDto adminSignUpDto) {

    UserResponseDto.SignUpDto signUoResponseDto = userAuthService.signUpAdmin(adminSignUpDto);

    return ApiResponse.onSuccess(CommonSuccessCode.OK, signUoResponseDto);
  }
}
