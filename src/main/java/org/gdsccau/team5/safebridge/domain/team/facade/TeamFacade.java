package org.gdsccau.team5.safebridge.domain.team.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.team.dto.request.TeamRequestDto.TeamCreateRequestDto;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamDataDto;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.team.service.TeamCommandService;
import org.gdsccau.team5.safebridge.domain.team.service.TeamQueryService;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.service.UserQueryService;
import org.gdsccau.team5.safebridge.domain.userTeam.service.UserTeamCommandService;
import org.gdsccau.team5.safebridge.domain.userTeam.service.UserTeamQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TeamFacade {

    private final TeamCommandService teamCommandService;
    private final TeamQueryService teamQueryService;
    private final UserQueryService userQueryService;
    private final UserTeamCommandService userTeamCommandService;
    private final UserTeamQueryService userTeamQueryService;
    private final RedisManager redisManager;

    @Transactional
    public void createTeam(final TeamCreateRequestDto requestDto) {
        List<User> users = userQueryService.findUsersByUserIds(requestDto.getUserIds());
        Team team = teamCommandService.createTeam(requestDto.getName());
        userTeamCommandService.batchInsertUserTeam(requestDto.getUserIds(), team.getId());
        users.forEach(
                user -> redisManager.initRedis(user.getId(), team.getId())
        );
    }

    @Transactional
    public void deleteTeam(final Long teamId) {
        userTeamQueryService.findAllUserIdByTeamId(teamId)
                .forEach(userId -> redisManager.updateRedisWhenDelete(userId, teamId));
        teamCommandService.deleteTeam(teamId);
    }

    @Transactional
    public TeamDataDto joinTeam(final Long userId, final Long teamId) {
        String teamName = teamQueryService.findNameByTeamId(teamId);
        int numberOfUsers = userTeamQueryService.countNumOfUsersByTeamId(teamId);
        userTeamCommandService.updateInRoomWhenJoin(userId, teamId);
        redisManager.updateRedisWhenJoin(userId, teamId);
        return teamCommandService.joinTeam(teamName, numberOfUsers);
    }

    @Transactional
    public void leaveTeam(final Long userId, final Long teamId) {
        userTeamCommandService.updateWhenLeave(userId, teamId);
        redisManager.updateRedisWhenLeave(userId, teamId);
        teamCommandService.leaveTeam(teamId, userId);
    }
}
