package org.gdsccau.team5.safebridge.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gdsccau.team5.safebridge.common.code.ErrorCode;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private ErrorCode errorCode;
}