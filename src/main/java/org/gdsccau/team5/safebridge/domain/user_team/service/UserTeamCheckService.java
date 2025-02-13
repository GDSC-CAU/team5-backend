package org.gdsccau.team5.safebridge.domain.user_team.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.user_team.repository.UserTeamRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTeamCheckService {

    private final UserTeamRepository userTeamRepository;

    public List<Long> findAllUserIdByTeamId(final Long teamId) {
        return userTeamRepository.findAllUserIdByTeamId(teamId);
    }
}
