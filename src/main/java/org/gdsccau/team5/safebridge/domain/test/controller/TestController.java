package org.gdsccau.team5.safebridge.domain.test.controller;

import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.code.success.TestSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.gdsccau.team5.safebridge.domain.test.dto.TestDto;
import org.gdsccau.team5.safebridge.domain.test.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    private final TestService testService;

    @GetMapping("/success")
    public ApiResponse<TestDto> apiSuccessTest() {
        TestDto testDto = testService.apiSuccessTest();
        return ApiResponse.onSuccess(TestSuccessCode.TEST_SUCCESS, testDto);
    }

    @GetMapping("/fail")
    public ApiResponse<Void> apiFailTest() {
        testService.apiFailTest();
        return ApiResponse.onSuccess(TestSuccessCode.TEST_SUCCESS);
    }
}
