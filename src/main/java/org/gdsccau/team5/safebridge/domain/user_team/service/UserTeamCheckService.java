package org.gdsccau.team5.safebridge.domain.user_team.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.user_team.entity.UserTeam;
import org.gdsccau.team5.safebridge.domain.user_team.repository.UserTeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTeamCheckService {

    private final UserTeamRepository userTeamRepository;

    @Transactional(readOnly = true)
    public List<Long> findAllUserIdByTeamId(final Long teamId) {
        return userTeamRepository.findAllUserIdByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public int countNumOfUsersByTeamId(final Long teamId) {
        return userTeamRepository.countNumOfUsersByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public UserTeam findUserTeamByUserIdAndTeamId(final Long userId, final Long teamId) {
        return userTeamRepository.findUserTeamByUserIdAndTeamId(userId, teamId).orElse(null);
    }

    @Transactional(readOnly = true)
    public LocalDateTime findAccessDateByUserIdAndTeamId(final Long userId, final Long teamId) {
        return userTeamRepository.findAccessDateByUserIdAndTeamId(userId, teamId).orElse(null);
    }
}
