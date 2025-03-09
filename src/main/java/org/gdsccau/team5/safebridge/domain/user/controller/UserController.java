package org.gdsccau.team5.safebridge.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.code.success.CommonSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.gdsccau.team5.safebridge.domain.user.dto.UserRequestDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserResponseDto;
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
      @RequestBody final UserRequestDto.LoginDto loginDto) {
    UserResponseDto.LoginDto loginResponseDto = userAuthService.login(loginDto);

    return ApiResponse.onSuccess(CommonSuccessCode.OK, loginResponseDto);
  }

  @PostMapping("/auth/user/signup")
  public ApiResponse<UserResponseDto.LoginDto> signUp(@RequestBody final UserRequestDto.UserSignUpDto userSignUpDto) {

    UserResponseDto.LoginDto signUoResponseDto = userAuthService.signUpUser(userSignUpDto);

    return ApiResponse.onSuccess(CommonSuccessCode.OK, signUoResponseDto);
  }
//
//    @PostMapping("/auth/admin/signup")
//    public ApiResponse<Void> adminSignUp(@RequestBody final UserRequestDto userRequestDto) {
//        User user = User.builder()
//            .name(userRequestDto.getName())
//            .language(userRequestDto.getLanguage())
//            .build();
//        userRepository.save(user);
//        return ApiResponse.onSuccess(CommonSuccessCode.OK);
//    }
}
