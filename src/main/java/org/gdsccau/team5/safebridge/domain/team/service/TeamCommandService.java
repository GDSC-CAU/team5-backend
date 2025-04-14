package org.gdsccau.team5.safebridge.domain.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamDataDto;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.team.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamCommandService {

    private final TeamRepository teamRepository;

    public Long createTeam(final String teamName) {
        Team team = Team.builder()
                .name(teamName)
                .build();
        log.info("채팅방 {} 생성하기", teamName);
        return teamRepository.save(team).getId();
    }

    public void deleteTeam(final Long teamId) {
        teamRepository.deleteById(teamId);
        log.info("채팅방 {} 삭제하기", teamId);
    }

    public TeamDataDto joinTeam(final String teamName, final int numberOfUsers) {
        log.info("채팅방 {} 접속하기", teamName);
        return this.createTeamDataDto(teamName, numberOfUsers);
    }

    public void leaveTeam(final Long teamId, final Long userId) {
        log.info("{}가 채팅방 {} 나가기", userId, teamId);
    }

    private TeamDataDto createTeamDataDto(final String teamName, final int numberOfUsers) {
        return TeamDataDto.builder()
                .teamName(teamName)
                .numberOfUsers(numberOfUsers)
                .build();
    }
}
