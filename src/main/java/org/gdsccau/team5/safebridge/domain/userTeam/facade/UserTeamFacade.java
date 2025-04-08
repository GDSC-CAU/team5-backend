package org.gdsccau.team5.safebridge.domain.userTeam.facade;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.ChatMetaDataDto;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatQueryService;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamListDto;
import org.gdsccau.team5.safebridge.domain.team.service.TeamQueryService;
import org.gdsccau.team5.safebridge.domain.userTeam.service.UserTeamQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTeamFacade {

    private final UserTeamQueryService userTeamQueryService;
    private final TeamQueryService teamQueryService;
    private final ChatQueryService chatQueryService;
    private final RedisManager redisManager;

    public List<TeamListDto> findAllTeamDataByUserId(final Long userId) {
        // TODO Redis에 없으면 DB를 조회한다.
        String teamListKey = redisManager.getTeamListKey(userId);
        return Objects.requireNonNull(redisManager.getTeamList(teamListKey)).stream()
                .map(Long::parseLong)
                .map(teamId -> {
                    String unReadMessageKey = redisManager.getUnReadMessageKey(userId, teamId);
                    int unReadMessage = redisManager.getUnReadMessageOrDefault(unReadMessageKey,
                            () -> userTeamQueryService.findUnReadMessageByUserIdAndTeamId(userId, teamId));
                    String teamName = teamQueryService.findNameByTeamId(teamId);
                    int numberOfUsers = userTeamQueryService.countNumOfUsersByTeamId(teamId);
                    ChatMetaDataDto chatMetaDataDto = chatQueryService.findChatMetaDataByTeamId(teamId);
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
