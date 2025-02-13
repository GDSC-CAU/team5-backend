package org.gdsccau.team5.safebridge.common.code.success;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gdsccau.team5.safebridge.common.code.SuccessCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TeamSuccessCode implements SuccessCode {

    CREATE_TEAM(HttpStatus.OK, "TEAM2001", "채팅방 생성하기"),
    JOIN_TEAM(HttpStatus.OK, "TEAM2002", "채팅방 입장하기"),
    LEAVE_TEAM(HttpStatus.OK, "TEAM2002", "채팅방 나가기");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getName() {
        return this.name();
    }
}
