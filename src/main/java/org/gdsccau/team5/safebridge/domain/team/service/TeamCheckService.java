package org.gdsccau.team5.safebridge.domain.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.team.repository.TeamRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamCheckService {

    private final TeamRepository teamRepository;

    public String findNameByTeamId(final Long teamId) {
        return teamRepository.findNameByTeamId(teamId).orElse(null);
    }
}
