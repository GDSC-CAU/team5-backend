package org.gdsccau.team5.safebridge.common.code;

import org.springframework.http.HttpStatus;

public interface SuccessCode {

    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();

    String getName();
}
