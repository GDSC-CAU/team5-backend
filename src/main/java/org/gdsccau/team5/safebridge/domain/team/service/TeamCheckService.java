package org.gdsccau.team5.safebridge.domain.team.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.code.error.TeamErrorCode;
import org.gdsccau.team5.safebridge.common.exception.handler.ExceptionHandler;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.team.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamCheckService {

    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public Team findByTeamId(final Long teamId) {
        Team team = teamRepository.findById(teamId).orElse(null);
        this.validateTeamData(team);
        return team;
    }

    @Transactional(readOnly = true)
    public String findNameByTeamId(final Long teamId) {
        String teamName = teamRepository.findNameByTeamId(teamId).orElse(null);
        this.validateTeamData(teamName);
        return teamName;
    }

    private <T> void validateTeamData(final T data) {
        if (data == null) {
            throw new ExceptionHandler(TeamErrorCode.TEAM_NOT_FOUND);
        }
    }

    private <T> void validateTeamData(final List<T> data) {
        if (data.isEmpty()) {
            throw new ExceptionHandler(TeamErrorCode.TEAM_NOT_FOUND);
        }
    }
}
