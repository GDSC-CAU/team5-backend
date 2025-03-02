package org.gdsccau.team5.safebridge.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.code.success.CommonSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.gdsccau.team5.safebridge.domain.user.dto.UserRequestDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserResponseDto;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping("/user/login")
    public ApiResponse<UserResponseDto> login(@RequestBody final UserRequestDto userRequestDto) {
        User user = userRepository.findByUsername(userRequestDto.getName());
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .userId(user.getId())
                .build();
        return ApiResponse.onSuccess(CommonSuccessCode.OK, userResponseDto);
    }

    @PostMapping("/user/signup")
    public ApiResponse<Void> signup(@RequestBody final UserRequestDto userRequestDto) {
        User user = User.builder()
                .name(userRequestDto.getName())
                .language(userRequestDto.getLanguage())
                .build();
        userRepository.save(user);
        return ApiResponse.onSuccess(CommonSuccessCode.OK);
    }
}
