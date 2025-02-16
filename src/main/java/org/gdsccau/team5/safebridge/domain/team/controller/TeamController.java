package org.gdsccau.team5.safebridge.domain.team.controller;

import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.code.success.TeamSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.gdsccau.team5.safebridge.domain.team.dto.request.TeamRequestDto.TeamCreateRequestDto;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamDataDto;
import org.gdsccau.team5.safebridge.domain.team.service.TeamService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    @PostMapping("")
    public ApiResponse<Void> createTeam(@RequestBody final TeamCreateRequestDto requestDto) {
        teamService.createTeam(requestDto);
        return ApiResponse.onSuccess(TeamSuccessCode.CREATE_TEAM);
    }

    @DeleteMapping("/{teamId}")
    public ApiResponse<Void> deleteTeam(@PathVariable final Long teamId) {
        teamService.deleteTeam(teamId);
        return ApiResponse.onSuccess(TeamSuccessCode.DELETE_TEAM);
    }

    @PostMapping("/{teamId}/join/{userId}")
    public ApiResponse<TeamDataDto> joinTeam(@PathVariable(name = "teamId") final Long teamId,
                                             @PathVariable(name = "userId") final Long userId) {
        TeamDataDto teamDataDto = teamService.joinTeam(teamId, userId);
        return ApiResponse.onSuccess(TeamSuccessCode.JOIN_TEAM, teamDataDto);
    }

    @PostMapping("/{teamId}/leave/{userId}")
    public ApiResponse<Void> leaveTeam(@PathVariable(name = "teamId") final Long teamId,
                                       @PathVariable(name = "userId") final Long userId) {
        teamService.leaveTeam(teamId, userId);
        return ApiResponse.onSuccess(TeamSuccessCode.LEAVE_TEAM);
    }
}
