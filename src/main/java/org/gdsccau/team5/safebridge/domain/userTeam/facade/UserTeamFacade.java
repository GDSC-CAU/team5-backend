package org.gdsccau.team5.safebridge.domain.userTeam.facade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.ChatMetaDataDto;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatQueryService;
import org.gdsccau.team5.safebridge.domain.team.dto.TeamDto;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamListDto;
import org.gdsccau.team5.safebridge.domain.team.service.TeamQueryService;
import org.gdsccau.team5.safebridge.domain.userTeam.service.UserTeamQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserTeamFacade {

    private final UserTeamQueryService userTeamQueryService;
    private final TeamQueryService teamQueryService;
    private final ChatQueryService chatQueryService;
    private final RedisManager redisManager;

    public List<TeamListDto> findAllTeamDataByUserId(final Long userId) {
        String teamListKey = redisManager.getTeamListKey(userId);
        if (redisManager.hasTeamList(teamListKey)) {
            return getTeamListFromRedis(teamListKey, userId);
        } else {
            return getTeamListFromDB(userId);
        }
    }

    private List<TeamListDto> getTeamListFromDB(final Long userId) {
        log.info("Get TeamList From DB!");
        List<TeamDto.TeamOrderDto> teamList = userTeamQueryService.findAllTeamOrderByLastChatTime(userId);
        teamList.forEach(data -> {
            Long teamId = data.getTeamId();
            LocalDateTime lastChatTime = data.getLastChatTime();
            redisManager.updateTeamList(redisManager.getTeamListKey(userId), teamId, lastChatTime);
        });
        return teamList.stream()
                .map(data -> {
                    Long teamId = data.getTeamId();
                    return createTeamListDto(userId, teamId);
                })
                .toList();
    }

    private List<TeamListDto> getTeamListFromRedis(final String teamListKey, final Long userId) {
        log.info("Get TeamList From Redis!");
        return Objects.requireNonNull(redisManager.getTeamList(teamListKey)).stream()
                .map(Long::parseLong)
                .map(teamId -> {
                    return createTeamListDto(userId, teamId);
                })
                .toList();
    }

    private TeamListDto createTeamListDto(final Long userId, final Long teamId) {
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
    }
}
