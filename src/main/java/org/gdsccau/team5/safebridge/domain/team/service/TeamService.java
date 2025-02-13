package org.gdsccau.team5.safebridge.domain.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.team.dto.request.TeamRequestDto.TeamCreateRequestDto;
import org.gdsccau.team5.safebridge.domain.team.repository.TeamRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public void createTeam(final Long teamId, final TeamCreateRequestDto requestDto) {
        // 1. List<userId>로 List<User> 엔티티를 가져온다.

        // 2. Team 엔티티를 생성한다.

        // 3. 모든 User에 대해 User_Team 엔티티를 생성한다.

        // 4. userId + teamId를 Key로 Redis에 unReadMessage=0, inRoom=0 값 생성하기

        // 5. DB 동기화 (?)
    }

    public void joinTeam(final Long teamId) {
        // 1. userId로 User 엔티티를 가져온다. (채팅방에 입장하는 사람)

        // 2. teamId로 Team 엔티티를 가져온다.

        // 3. userId + teamId를 Key로 Redis의 unReadMessage=0, inRoom=1로 변경

        // 4. DB 동기화 (?)
    }

    public void leaveTeam(final Long teamId) {
        // 1. userId로 User 엔티티를 가져온다. (채팅방에서 나가는 사람)

        // 2. teamId로 Team 엔티티를 가져온다.

        // 3. userId + teamId를 Key로 Redis의 inRoom=0로 변경

        // 4. DB 동기화 (?)
    }
}
