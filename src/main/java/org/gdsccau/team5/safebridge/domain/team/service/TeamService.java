package org.gdsccau.team5.safebridge.domain.team.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.team.dto.request.TeamRequestDto.TeamCreateRequestDto;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamDataDto;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.team.repository.TeamRepository;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.service.UserCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {

    private final UserTeamService userTeamService;
    private final UserTeamCheckService userTeamCheckService;
    private final UserCheckService userCheckService;
    private final TeamCheckService teamCheckService;
    private final TeamRepository teamRepository;
    private final RedisManager redisManager;

    @Transactional
    public void createTeam(final TeamCreateRequestDto requestDto) {
        List<User> users = requestDto.getUserIds().stream()
                .map(userCheckService::findByUserId)
                .toList();
        Team team = this.createTeam(requestDto.getName());
        users.forEach(
                user -> {
                    redisManager.initRedis(user.getId(), team.getId());
                    userTeamService.createUserTeam(user, team);
                }
        );
        log.info("채팅방 {} 생성하기", team.getName());
    }

    @Transactional
    public void deleteTeam(final Long teamId) {
        userTeamCheckService.findAllUserIdByTeamId(teamId)
                .forEach(userId -> redisManager.updateRedisWhenDelete(userId, teamId));
        teamRepository.deleteById(teamId);
        log.info("채팅방 {} 삭제하기", teamId);
    }

    @Transactional
    public TeamDataDto joinTeam(final Long teamId, final Long userId) {
        String teamName = teamCheckService.findNameByTeamId(teamId);
        int numberOfUsers = userTeamCheckService.countNumOfUsersByTeamId(teamId);
        redisManager.updateRedisWhenJoin(userId, teamId);

        log.info("{}가 채팅방 {} 접속하기", userId, teamId);
        // 4. DB 동기화

        return this.createTeamDataDto(teamName, numberOfUsers);
    }

    @Transactional
    public void leaveTeam(final Long teamId, final Long userId) {
        userTeamService.updateAccessDate(userId, teamId);
        redisManager.updateRedisWhenLeave(userId, teamId);
        log.info("{}가 채팅방 {} 나가기", userId, teamId);
        // 4. DB 동기화
    }

    private Team createTeam(final String name) {
        Team team = Team.builder()
                .name(name)
                .build();
        return teamRepository.save(team);
    }

    private TeamDataDto createTeamDataDto(final String teamName, final int numberOfUsers) {
        return TeamDataDto.builder()
                .teamName(teamName)
                .numberOfUsers(numberOfUsers)
                .build();
    }
}
