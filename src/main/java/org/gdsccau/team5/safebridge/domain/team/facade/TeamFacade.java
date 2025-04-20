package org.gdsccau.team5.safebridge.domain.team.facade;

import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.domain.team.dto.request.TeamRequestDto.TeamCreateRequestDto;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamDataDto;
import org.gdsccau.team5.safebridge.domain.team.event.TeamCreateEvent;
import org.gdsccau.team5.safebridge.domain.team.event.TeamDeleteEvent;
import org.gdsccau.team5.safebridge.domain.team.event.TeamJoinEvent;
import org.gdsccau.team5.safebridge.domain.team.event.TeamLeaveEvent;
import org.gdsccau.team5.safebridge.domain.team.service.TeamCommandService;
import org.gdsccau.team5.safebridge.domain.team.service.TeamQueryService;
import org.gdsccau.team5.safebridge.domain.userTeam.service.UserTeamCommandService;
import org.gdsccau.team5.safebridge.domain.userTeam.service.UserTeamQueryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamFacade {

    private final TeamCommandService teamCommandService;
    private final TeamQueryService teamQueryService;
    private final UserTeamCommandService userTeamCommandService;
    private final UserTeamQueryService userTeamQueryService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createTeam(final TeamCreateRequestDto requestDto) {
        Long teamId = teamCommandService.createTeam(requestDto.getName());
        userTeamCommandService.batchInsertUserTeam(requestDto.getUserIds(), teamId);
        eventPublisher.publishEvent(new TeamCreateEvent(this, requestDto.getUserIds(), teamId));
    }

    @Transactional
    public void deleteTeam(final Long teamId) {
        teamCommandService.deleteTeam(teamId);
        List<Long> userIds = userTeamQueryService.findAllUserIdByTeamId(teamId);
        eventPublisher.publishEvent(new TeamDeleteEvent(this, userIds, teamId));
    }

    @Transactional
    public TeamDataDto joinTeam(final Long userId, final Long teamId) {
        String teamName = teamQueryService.findNameByTeamId(teamId);
        int numberOfUsers = userTeamQueryService.countNumOfUsersByTeamId(teamId);
        userTeamCommandService.updateInRoomWhenJoin(userId, teamId);
        TeamDataDto teamDataDto = teamCommandService.joinTeam(teamName, numberOfUsers);
        eventPublisher.publishEvent(new TeamJoinEvent(this, userId, teamId));
        return teamDataDto;
    }

    @Transactional
    public void leaveTeam(final Long userId, final Long teamId) {
        userTeamCommandService.updateWhenLeave(userId, teamId);
        teamCommandService.leaveTeam(teamId, userId);
        eventPublisher.publishEvent(new TeamLeaveEvent(this, userId, teamId));
    }
}
