package org.gdsccau.team5.safebridge.common.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gdsccau.team5.safebridge.common.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    LOGIN_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH4001", "User Not Found"),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4002", "User AUTHENTICATE FAILURE"),
    LOGIN_ID_ALREADY_EXIST(HttpStatus.CONFLICT,"AUTH4003" ,"User Login Id Already Exist" );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getName() {
        return this.name();
    }

}
