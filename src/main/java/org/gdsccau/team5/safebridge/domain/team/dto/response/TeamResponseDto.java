package org.gdsccau.team5.safebridge.domain.team.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TeamResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamListDto {
        Long teamId;
        String teamName;
        String lastChat;
        LocalDateTime lastChatTime;
    }
}
