package org.gdsccau.team5.safebridge.domain.user_team.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.code.error.UserTeamErrorCode;
import org.gdsccau.team5.safebridge.common.exception.handler.ExceptionHandler;
import org.gdsccau.team5.safebridge.domain.team.dto.TeamDto.TeamOrderDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserDto.UserIdAndLanguageDto;
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
        List<Long> userIds = userTeamRepository.findAllUserIdByTeamId(teamId);
        this.validateUserTeamData(userIds);
        return userIds;
    }

    @Transactional(readOnly = true)
    public List<Long> findAllUserIdWithTeam() {
        List<Long> userIds = userTeamRepository.findAllUserIdWithTeam();
        this.validateUserTeamData(userIds);
        return userIds;
    }

    @Transactional(readOnly = true)
    public List<Long> findAllTeamIdByUserId(final Long userId) {
        List<Long> teamIds = userTeamRepository.findAllTeamIdByUserId(userId);
        this.validateUserTeamData(teamIds);
        return teamIds;
    }

    @Transactional(readOnly = true)
    public List<UserIdAndLanguageDto> findAllUserIdAndLanguageByTeamId(final Long teamId) {
        List<UserIdAndLanguageDto> dtos = userTeamRepository.findAllUserIdAndLanguageByTeamId(teamId);
        this.validateUserTeamData(dtos);
        return dtos;
    }

    @Transactional(readOnly = true)
    public int countNumOfUsersByTeamId(final Long teamId) {
        Integer numOfUsers = userTeamRepository.countNumOfUsersByTeamId(teamId);
        this.validateUserTeamData(numOfUsers);
        return numOfUsers;
    }

    @Transactional(readOnly = true)
    public UserTeam findUserTeamByUserIdAndTeamId(final Long userId, final Long teamId) {
        UserTeam userTeam = userTeamRepository.findUserTeamByUserIdAndTeamId(userId, teamId).orElse(null);
        this.validateUserTeamData(userTeam);
        return userTeam;
    }

    @Transactional(readOnly = true)
    public LocalDateTime findAccessDateByUserIdAndTeamId(final Long userId, final Long teamId) {
        LocalDateTime accessDate = userTeamRepository.findAccessDateByUserIdAndTeamId(userId, teamId).orElse(null);
        this.validateUserTeamData(accessDate);
        return accessDate;
    }

    @Transactional(readOnly = true)
    public Integer findInRoomByUserIdAndTeamId(final Long userId, final Long teamId) {
        Integer inRoom = userTeamRepository.findInRoomByUserIdAndTeamId(userId, teamId).orElse(null);
        this.validateUserTeamData(inRoom);
        return inRoom;
    }

    @Transactional(readOnly = true)
    public Integer findUnReadMessageByUserIdAndTeamId(final Long userId, final Long teamId) {
        Integer unReadMessage = userTeamRepository.findUnReadMessageByUserIdAndTeamId(userId, teamId).orElse(null);
        this.validateUserTeamData(unReadMessage);
        return unReadMessage;
    }

    @Transactional(readOnly = true)
    public List<TeamOrderDto> findAllTeamOrderByLastChatTime() {
        List<TeamOrderDto> dtos = userTeamRepository.findAllTeamOrderByLastChatTime();
        this.validateUserTeamData(dtos);
        return dtos;
    }

    private <T> void validateUserTeamData(final T data) {
        if (data == null) {
            throw new ExceptionHandler(UserTeamErrorCode.USER_TEAM_NOT_FOUND);
        }
    }

    private <T> void validateUserTeamData(final List<T> datas) {
        if (datas.isEmpty()) {
            throw new ExceptionHandler(UserTeamErrorCode.USER_TEAM_NOT_FOUND);
        }
    }
}
