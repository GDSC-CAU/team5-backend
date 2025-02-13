package org.gdsccau.team5.safebridge.domain.team.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.team.dto.request.TeamRequestDto.TeamCreateRequestDto;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.team.repository.TeamRepository;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.service.UserCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {

    private final UserTeamService userTeamService;
    private final UserTeamCheckService userTeamCheckService;
    private final UserCheckService userCheckService;
    private final TeamCheckService teamCheckService;
    private final TeamRepository teamRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void createTeam(final TeamCreateRequestDto requestDto) {
        List<User> users = requestDto.getUserIds().stream()
                .map(userCheckService::findByUserId)
                .toList();
        Team team = this.createTeam(requestDto.getName());
        users.forEach(
                user -> {
                    userTeamService.createUserTeam(user, team);
                    this.initRedis(user, team);
                }
        );
    }

    @Transactional
    public void deleteTeam(final Long teamId) {
        Team team = teamCheckService.findByTeamId(teamId);
        userTeamCheckService.findAllUserIdByTeamId(teamId).stream()
                .map(userCheckService::findByUserId)
                .forEach(user -> this.updateRedisWhenDelete(user, team));
        teamRepository.deleteById(teamId);
    }

    @Transactional
    public void joinTeam(final Long teamId) {
        Long userId = 1L;
        User user = userCheckService.findByUserId(userId);
        Team team = teamCheckService.findByTeamId(teamId);
        this.updateRedisWhenJoin(user, team);

        // 4. DB 동기화
    }

    @Transactional
    public void leaveTeam(final Long teamId) {
        Long userId = 1L;
        User user = userCheckService.findByUserId(userId);
        Team team = teamCheckService.findByTeamId(teamId);
        this.updateRedisWhenLeave(user, team);

        // 4. DB 동기화
    }

    @Transactional
    public Team createTeam(final String name) {
        Team team = Team.builder()
                .name(name)
                .build();
        return teamRepository.save(team);
    }

    private void initRedis(final User user, final Team team) {
        String inRoomKey = this.getInRoomKey(user, team);
        String unReadMessageKey = this.getUnReadMessageKey(user, team);
        String zSetKey = this.getZSetKey(user);
        long score = LocalDateTime.now()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli();
        redisTemplate.opsForValue().set(inRoomKey, "0");
        redisTemplate.opsForValue().set(unReadMessageKey, "0");
        redisTemplate.opsForZSet().add(zSetKey, String.valueOf(team.getId()), score);
    }

    private void updateRedisWhenJoin(final User user, final Team team) {
        String inRoomKey = this.getInRoomKey(user, team);
        String unReadMessageKey = this.getUnReadMessageKey(user, team);
        redisTemplate.opsForValue().set(inRoomKey, "1");
        redisTemplate.opsForValue().set(unReadMessageKey, "0");
    }

    private void updateRedisWhenLeave(final User user, final Team team) {
        String inRoomKey = this.getInRoomKey(user, team);
        String unReadMessageKey = this.getUnReadMessageKey(user, team);
        redisTemplate.opsForValue().set(inRoomKey, "0");
        redisTemplate.opsForValue().set(unReadMessageKey, "0");
    }

    private void updateRedisWhenDelete(final User user, final Team team) {
        String inRoomKey = this.getInRoomKey(user, team);
        String unReadMessageKey = this.getUnReadMessageKey(user, team);
        String zSetKey = this.getZSetKey(user);
        redisTemplate.delete(inRoomKey);
        redisTemplate.delete(unReadMessageKey);
        redisTemplate.delete(zSetKey);
    }

    private String getInRoomKey(final User user, final Team team) {
        return "userId:" + user.getId() + "teamId:" + team.getId() + "inRoom";
    }

    private String getUnReadMessageKey(final User user, final Team team) {
        return "userId:" + user.getId() + "teamId:" + team.getId() + "unReadMessage";
    }

    private String getZSetKey(final User user) {
        return "userId:" + user.getId() + "team";
    }
}
