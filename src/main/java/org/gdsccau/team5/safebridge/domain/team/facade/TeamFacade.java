package org.gdsccau.team5.safebridge.domain.team.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.team.dto.request.TeamRequestDto.TeamCreateRequestDto;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamDataDto;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.team.service.TeamCheckService;
import org.gdsccau.team5.safebridge.domain.team.service.TeamService;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.service.UserCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TeamFacade {

    private final TeamService teamService;
    private final TeamCheckService teamCheckService;
    private final UserCheckService userCheckService;
    private final UserTeamService userTeamService;
    private final UserTeamCheckService userTeamCheckService;
    private final RedisManager redisManager;

    @Transactional
    public void createTeam(final TeamCreateRequestDto requestDto) {
        List<User> users = requestDto.getUserIds().stream()
                .map(userCheckService::findByUserId)
                .toList();
        Team team = teamService.createTeam(requestDto.getName());
        users.forEach(
                user -> {
                    redisManager.initRedis(user.getId(), team.getId());
                    userTeamService.createUserTeam(user, team);
                }
        );
    }

    @Transactional
    public void deleteTeam(final Long teamId) {
        userTeamCheckService.findAllUserIdByTeamId(teamId)
                .forEach(userId -> redisManager.updateRedisWhenDelete(userId, teamId));
        teamService.deleteTeam(teamId);
    }

    @Transactional
    public TeamDataDto joinTeam(final Long userId, final Long teamId) {
        String teamName = teamCheckService.findNameByTeamId(teamId);
        int numberOfUsers = userTeamCheckService.countNumOfUsersByTeamId(teamId);
        redisManager.updateRedisWhenJoin(userId, teamId);
        userTeamService.updateInRoomWhenJoin(userId, teamId);
        return teamService.joinTeam(teamName, numberOfUsers);
    }

    @Transactional
    public void leaveTeam(final Long userId, final Long teamId) {
        userTeamService.updateAccessDate(userId, teamId);
        redisManager.updateRedisWhenLeave(userId, teamId);
        userTeamService.updateInRoomWhenLeave(userId, teamId);
        teamService.leaveTeam(teamId, userId);
    }
}
