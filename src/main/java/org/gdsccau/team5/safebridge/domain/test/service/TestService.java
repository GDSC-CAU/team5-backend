package org.gdsccau.team5.safebridge.domain.test.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.code.error.CommonErrorCode;
import org.gdsccau.team5.safebridge.common.code.error.TestErrorCode;
import org.gdsccau.team5.safebridge.common.exception.handler.ExceptionHandler;
import org.gdsccau.team5.safebridge.domain.test.dto.TestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class TestService {

    public TestDto apiSuccessTest() {
        return new TestDto("API 테스트 성공");
    }

    public void apiFailTest() {
        /*
        비즈니스 로직 진행 중 에러 발생 -> ExceptionHandler 던지기
         */
        throw new ExceptionHandler(TestErrorCode.TEST_ERROR);
    }
}
