package org.gdsccau.team5.safebridge.common.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gdsccau.team5.safebridge.common.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserAdminErrorCode implements ErrorCode {

  ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_ADMIN4001", "admin not found"),
  EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_ADMIN4002", "employee not found"),
  USER_ADIMIN_ALREADY_EXIST(HttpStatus.CONFLICT, "USER_ADMIN4003",
      "Admin-employee relation already exist");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public String getName() {
    return this.name();
  }
}
