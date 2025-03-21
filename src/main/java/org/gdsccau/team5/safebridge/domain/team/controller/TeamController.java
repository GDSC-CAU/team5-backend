package org.gdsccau.team5.safebridge.domain.team.controller;

import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.code.success.TeamSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.gdsccau.team5.safebridge.domain.team.dto.request.TeamRequestDto.TeamCreateRequestDto;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamDataDto;
import org.gdsccau.team5.safebridge.domain.team.facade.TeamFacade;
import org.gdsccau.team5.safebridge.domain.team.service.TeamService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final TeamFacade teamFacade;

    @PostMapping("")
    public ApiResponse<Void> createTeam(@RequestBody final TeamCreateRequestDto requestDto) {
        teamFacade.createTeam(requestDto);
        return ApiResponse.onSuccess(TeamSuccessCode.CREATE_TEAM);
    }

    @DeleteMapping("/{teamId}")
    public ApiResponse<Void> deleteTeam(@PathVariable final Long teamId) {
        teamFacade.deleteTeam(teamId);
        return ApiResponse.onSuccess(TeamSuccessCode.DELETE_TEAM);
    }

    @PostMapping("/{teamId}/users/{userId}/join")
    public ApiResponse<TeamDataDto> joinTeam(@PathVariable(name = "teamId") final Long teamId,
                                             @PathVariable(name = "userId") final Long userId) {
        TeamDataDto teamDataDto = teamFacade.joinTeam(userId, teamId);
        return ApiResponse.onSuccess(TeamSuccessCode.JOIN_TEAM, teamDataDto);
    }

    @PostMapping("/{teamId}/users/{userId}/leave")
    public ApiResponse<Void> leaveTeam(@PathVariable(name = "teamId") final Long teamId,
                                       @PathVariable(name = "userId") final Long userId) {
        teamFacade.leaveTeam(userId, teamId);
        return ApiResponse.onSuccess(TeamSuccessCode.LEAVE_TEAM);
    }
}
