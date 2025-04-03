package org.gdsccau.team5.safebridge.domain.userTeam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserTeamDto {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UserTeamUnReadMessageDto {
        Long userId;
        Long teamId;
        int unReadMessage;
    }
}
