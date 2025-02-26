package org.gdsccau.team5.safebridge.common.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gdsccau.team5.safebridge.common.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReportErrorCode implements ErrorCode {

    REPORT_FILE_NOT_CORRECT(HttpStatus.INTERNAL_SERVER_ERROR, "REPORT4001", "Report audio file seems incorrect");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getName() {
        return this.name();
    }
}
