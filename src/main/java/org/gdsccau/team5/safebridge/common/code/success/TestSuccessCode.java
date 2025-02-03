package org.gdsccau.team5.safebridge.common.code.success;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gdsccau.team5.safebridge.common.code.SuccessCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TestSuccessCode implements SuccessCode {

    TEST_SUCCESS(HttpStatus.OK, "TEST200", "API 테스트 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getName() {
        return this.name();
    }
}
