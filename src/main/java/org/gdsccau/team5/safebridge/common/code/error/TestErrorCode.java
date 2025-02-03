package org.gdsccau.team5.safebridge.common.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gdsccau.team5.safebridge.common.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TestErrorCode implements ErrorCode {

    TEST_ERROR(HttpStatus.BAD_REQUEST, "TEST400", "API 테스트 실패");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getName(){
        return this.name();
    }
}
