package org.gdsccau.team5.safebridge.domain.user.exception;

import org.gdsccau.team5.safebridge.common.code.ErrorCode;
import org.gdsccau.team5.safebridge.common.exception.GeneralException;

public class LoginUserNotFoundException extends GeneralException implements UserAuthException{

  public LoginUserNotFoundException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
