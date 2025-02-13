package org.gdsccau.team5.safebridge.common.code.success;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gdsccau.team5.safebridge.common.code.SuccessCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatSuccessCode implements SuccessCode {

    FIND_CHAT_IN_TEAM(HttpStatus.OK, "CHAT2001", "채팅방에 속한 모든 채팅 가져오기 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getName() {
        return this.name();
    }
}
