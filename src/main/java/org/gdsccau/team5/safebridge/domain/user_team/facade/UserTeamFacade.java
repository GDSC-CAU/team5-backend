package org.gdsccau.team5.safebridge.domain.user_team.facade;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.ChatMetaDataDto;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatCheckService;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamListDto;
import org.gdsccau.team5.safebridge.domain.team.service.TeamCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTeamFacade {

    private final UserTeamService userTeamService;
    private final UserTeamCheckService userTeamCheckService;
    private final TeamCheckService teamCheckService;
    private final ChatCheckService chatCheckService;
    private final RedisManager redisManager;

    public List<TeamListDto> findAllTeamDataByUserId(final Long userId) {
        String teamListKey = redisManager.getTeamListKey(userId);
        return Objects.requireNonNull(redisManager.getTeamList(teamListKey)).stream()
                .map(Long::parseLong)
                .map(teamId -> {
                    String unReadMessageKey = redisManager.getUnReadMessageKey(userId, teamId);
                    int unReadMessage = redisManager.getUnReadMessageOrDefault(unReadMessageKey,
                            () -> userTeamCheckService.findUnReadMessageByUserIdAndTeamId(userId, teamId));
                    String teamName = teamCheckService.findNameByTeamId(teamId);
                    int numberOfUsers = userTeamCheckService.countNumOfUsersByTeamId(teamId);
                    ChatMetaDataDto chatMetaDataDto = chatCheckService.findChatMetaDataByTeamId(teamId);
                    return TeamListDto.builder()
                            .teamId(teamId)
                            .teamName(teamName)
                            .lastChat(chatMetaDataDto.getLastChat())
                            .lastChatTime(chatMetaDataDto.getLastChatTime())
                            .unReadMessage(unReadMessage)
                            .numberOfUsers(numberOfUsers)
                            .build();
                })
                .toList();
    }
}
