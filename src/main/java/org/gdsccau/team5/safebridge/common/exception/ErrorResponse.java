package org.gdsccau.team5.safebridge.common.exception;

import java.time.LocalDateTime;

public class ErrorResponse {

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  private final LocalDateTime timestamp;
  private final int status;
  private final String message;

  public ErrorResponse(int status, String message) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.message = message;
  }
}
