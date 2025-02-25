package org.gdsccau.team5.safebridge.common.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gdsccau.team5.safebridge.common.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserTeamErrorCode implements ErrorCode {

    USER_TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "USERTEAM4001", "UserTeam Data Not Found");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getName() {
        return this.name();
    }
}
