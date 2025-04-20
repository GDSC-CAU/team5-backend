package org.gdsccau.team5.safebridge.domain.userTeam.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.code.error.UserTeamErrorCode;
import org.gdsccau.team5.safebridge.common.exception.handler.ExceptionHandler;
import org.gdsccau.team5.safebridge.domain.team.dto.TeamDto.TeamOrderDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserDto.UserIdAndLanguageDto;
import org.gdsccau.team5.safebridge.domain.userTeam.entity.UserTeam;
import org.gdsccau.team5.safebridge.domain.userTeam.repository.UserTeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTeamQueryService {

    private final UserTeamRepository userTeamRepository;

    public List<Long> findAllUserIdByTeamId(final Long teamId) {
        List<Long> userIds = userTeamRepository.findAllUserIdByTeamId(teamId);
        this.validateUserTeamData(userIds);
        return userIds;
    }

    public List<Long> findAllTeamIdByUserId(final Long userId) {
        List<Long> teamIds = userTeamRepository.findAllTeamIdByUserId(userId);
        this.validateUserTeamData(teamIds);
        return teamIds;
    }

    public List<UserIdAndLanguageDto> findAllUserIdAndLanguageByTeamId(final Long teamId) {
        List<UserIdAndLanguageDto> dtos = userTeamRepository.findAllUserIdAndLanguageByTeamId(teamId);
        this.validateUserTeamData(dtos);
        return dtos;
    }

    public int countNumOfUsersByTeamId(final Long teamId) {
        Integer numOfUsers = userTeamRepository.countNumOfUsersByTeamId(teamId);
        this.validateUserTeamData(numOfUsers);
        return numOfUsers;
    }

    public UserTeam findUserTeamByUserIdAndTeamId(final Long userId, final Long teamId) {
        UserTeam userTeam = userTeamRepository.findUserTeamByUserIdAndTeamId(userId, teamId).orElse(null);
        this.validateUserTeamData(userTeam);
        return userTeam;
    }

    public LocalDateTime findAccessDateByUserIdAndTeamId(final Long userId, final Long teamId) {
        LocalDateTime accessDate = userTeamRepository.findAccessDateByUserIdAndTeamId(userId, teamId).orElse(null);
        this.validateUserTeamData(accessDate);
        return accessDate;
    }

    public Integer findInRoomByUserIdAndTeamId(final Long userId, final Long teamId) {
        Integer inRoom = userTeamRepository.findInRoomByUserIdAndTeamId(userId, teamId).orElse(null);
        this.validateUserTeamData(inRoom);
        return inRoom;
    }

    public Integer findUnReadMessageByUserIdAndTeamId(final Long userId, final Long teamId) {
        Integer unReadMessage = userTeamRepository.findUnReadMessageByUserIdAndTeamId(userId, teamId).orElse(null);
        this.validateUserTeamData(unReadMessage);
        return unReadMessage;
    }

    public List<TeamOrderDto> findAllTeamOrderByLastChatTime(final Long userId) {
        List<TeamOrderDto> dtos = userTeamRepository.findAllTeamOrderByLastChatTime(userId);
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
