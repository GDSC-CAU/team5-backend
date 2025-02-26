package org.gdsccau.team5.safebridge.common.code.success;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gdsccau.team5.safebridge.common.code.SuccessCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReportSuccessCode implements SuccessCode {

    REPORT_SUCCESS_CODE(HttpStatus.OK, "REPORT2001", "신고하기 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getName() {
        return this.name();
    }
}
