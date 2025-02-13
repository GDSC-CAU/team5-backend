package org.gdsccau.team5.safebridge.domain.team.controller;

import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.code.success.TeamSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.gdsccau.team5.safebridge.domain.team.dto.request.TeamRequestDto.TeamCreateRequestDto;
import org.gdsccau.team5.safebridge.domain.team.service.TeamService;
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

    @PostMapping("/{teamId}")
    public ApiResponse<Void> createTeam(
            @PathVariable final Long teamId,
            @RequestBody final TeamCreateRequestDto requestDto) {
        teamService.createTeam(teamId, requestDto);
        return ApiResponse.onSuccess(TeamSuccessCode.CREATE_TEAM);
    }

    @PostMapping("/{teamId}/join")
    public ApiResponse<Void> joinTeam(@PathVariable final Long teamId) {
        teamService.joinTeam(teamId);
        return ApiResponse.onSuccess(TeamSuccessCode.JOIN_TEAM);
    }

    @PostMapping("/{teamId}/leave")
    public ApiResponse<Void> leaveTeam(@PathVariable final Long teamId) {
        teamService.leaveTeam(teamId);
        return ApiResponse.onSuccess(TeamSuccessCode.LEAVE_TEAM);
    }
}
