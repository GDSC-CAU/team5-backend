package org.gdsccau.team5.safebridge.common.code.success;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gdsccau.team5.safebridge.common.code.SuccessCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserTeamSuccessCode implements SuccessCode {

    GET_TEAM_LIST(HttpStatus.OK, "USER_TEAM2001", "사용자가 속한 팀 불러오기 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getName() {
        return this.name();
    }
}
