package org.gdsccau.team5.safebridge.common.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gdsccau.team5.safebridge.common.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TermErrorCode implements ErrorCode {

    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "TERM4001", "Term Data Not Found");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getName() {
        return this.name();
    }
}
