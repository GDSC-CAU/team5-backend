package org.gdsccau.team5.safebridge.domain.user_team.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.code.success.UserTeamSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamListDto;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserTeamController {

    private final UserTeamCheckService userTeamCheckService;

    // 채팅방 리스트 페이지로 이동했을 때 초기 데이터 받아오기
    @GetMapping("/user-teams/lists/{userId}")
    public ApiResponse<List<TeamListDto>> getUserTeams(@PathVariable(name = "userId") final Long userId) {
        List<TeamListDto> teamListDtos = userTeamCheckService.findAllTeamDataByUserId(userId);
        return ApiResponse.onSuccess(UserTeamSuccessCode.GET_TEAM_LIST, teamListDtos);
    }
}
