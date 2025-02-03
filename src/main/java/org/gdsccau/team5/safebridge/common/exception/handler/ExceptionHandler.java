package org.gdsccau.team5.safebridge.common.exception.handler;

import org.gdsccau.team5.safebridge.common.code.ErrorCode;
import org.gdsccau.team5.safebridge.common.exception.GeneralException;

public class ExceptionHandler extends GeneralException {
    public ExceptionHandler(ErrorCode errorCode) {
        super(errorCode);
    }
}
