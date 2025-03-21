package org.gdsccau.team5.safebridge.domain.user_team.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.gdsccau.team5.safebridge.domain.user_team.dto.UserTeamDto.UserTeamUnReadMessageDto;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTeamConverter {

    public static UserTeamUnReadMessageDto toUserTeamUnReadMessageDto(final Long userId, final Long teamId,
                                                                      final int unReadMessage) {
        return UserTeamUnReadMessageDto.builder()
                .userId(userId)
                .teamId(teamId)
                .unReadMessage(unReadMessage)
                .build();
    }
}
