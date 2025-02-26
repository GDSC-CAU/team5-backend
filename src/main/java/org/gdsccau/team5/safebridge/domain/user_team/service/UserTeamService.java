package org.gdsccau.team5.safebridge.domain.user_team.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user_team.entity.UserTeam;
import org.gdsccau.team5.safebridge.domain.user_team.repository.UserTeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTeamService {

    private final UserTeamCheckService userTeamCheckService;
    private final UserTeamRepository userTeamRepository;

    @Transactional
    public void createUserTeam(final User user, final Team team) {
        UserTeam userTeam = UserTeam.builder()
                .inRoom(false)
                .accessDate(LocalDateTime.now())
                .unReadMessage(0)
                .user(user)
                .team(team)
                .build();
        userTeamRepository.save(userTeam);
    }

    @Transactional
    public void updateAccessDate(final Long userId, final Long teamId) {
        UserTeam userTeam = userTeamCheckService.findUserTeamByUserIdAndTeamId(userId, teamId);
        userTeam.updateAccessDate();
    }
}
