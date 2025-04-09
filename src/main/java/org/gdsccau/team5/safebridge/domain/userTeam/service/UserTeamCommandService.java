package org.gdsccau.team5.safebridge.domain.userTeam.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.userTeam.dto.UserTeamDto.UserTeamUnReadMessageDto;
import org.gdsccau.team5.safebridge.domain.userTeam.entity.UserTeam;
import org.gdsccau.team5.safebridge.domain.userTeam.repository.UserTeamBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTeamCommandService {

    private final UserTeamQueryService userTeamQueryService;
    private final UserTeamBatchRepository userTeamBatchRepository;

    @Transactional
    public void batchInsertUserTeam(final List<Long> userIds, final Long teamId) {
        userTeamBatchRepository.userTeamBatchInsert(userIds, teamId);
    }

    @Transactional
    public void updateInRoomWhenJoin(final Long userId, final Long teamId) {
        UserTeam userTeam = userTeamQueryService.findUserTeamByUserIdAndTeamId(userId, teamId);
        userTeam.updateInRoomWhenJoin();
    }

    @Transactional
    public void updateWhenLeave(final Long userId, final Long teamId) {
        UserTeam userTeam = userTeamQueryService.findUserTeamByUserIdAndTeamId(userId, teamId);
        userTeam.updateAccessDate();
        userTeam.updateInRoomWhenLeave();
    }

    @Transactional
    public void syncUnReadMessageToDB(final List<UserTeamUnReadMessageDto> dtos) {
        userTeamBatchRepository.unReadMessageBatchUpdate(dtos);
    }
}
