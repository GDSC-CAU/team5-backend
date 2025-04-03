package org.gdsccau.team5.safebridge.domain.team.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TeamDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamOrderDto{
        Long userId;
        Long teamId;
        LocalDateTime lastChatTime;
    }
}
