package org.gdsccau.team5.safebridge.domain.userTeam.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.userTeam.dto.UserTeamDto.UserTeamUnReadMessageDto;
import org.gdsccau.team5.safebridge.domain.userTeam.entity.UserTeam;
import org.gdsccau.team5.safebridge.domain.userTeam.repository.UserTeamBatchRepository;
import org.gdsccau.team5.safebridge.domain.userTeam.repository.UserTeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTeamCommandService {

    private final UserTeamQueryService userTeamQueryService;
    private final UserTeamRepository userTeamRepository;
    private final UserTeamBatchRepository userTeamBatchRepository;

    @Transactional
    public void createUserTeam(final User user, final Team team) {
        UserTeam userTeam = UserTeam.builder()
                .inRoom(0)
                .accessDate(LocalDateTime.now())
                .unReadMessage(0)
                .user(user)
                .team(team)
                .build();
        userTeamRepository.save(userTeam);
    }

    @Transactional
    public void updateAccessDate(final Long userId, final Long teamId) {
        UserTeam userTeam = userTeamQueryService.findUserTeamByUserIdAndTeamId(userId, teamId);
        userTeam.updateAccessDate();
    }

    @Transactional
    public void updateInRoomWhenJoin(final Long userId, final Long teamId) {
        UserTeam userTeam = userTeamQueryService.findUserTeamByUserIdAndTeamId(userId, teamId);
        userTeam.updateInRoomWhenJoin();
    }

    @Transactional
    public void updateInRoomWhenLeave(final Long userId, final Long teamId) {
        UserTeam userTeam = userTeamQueryService.findUserTeamByUserIdAndTeamId(userId, teamId);
        userTeam.updateInRoomWhenLeave();
    }

    @Transactional
    public void syncUnReadMessageToDB(final List<UserTeamUnReadMessageDto> dtos) {
        userTeamBatchRepository.unReadMessageBatchUpdate(dtos);
    }
}
