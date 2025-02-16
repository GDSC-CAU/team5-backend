package org.gdsccau.team5.safebridge.domain.chat.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.chat.converter.ChatConverter;
import org.gdsccau.team5.safebridge.domain.chat.dto.request.ChatRequestDto.ChatMessageRequestDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.repository.ChatRepository;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamListDto;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.team.service.TeamCheckService;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.service.UserCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final UserTeamCheckService userTeamCheckService;
    private final UserCheckService userCheckService;
    private final TeamCheckService teamCheckService;
    private final ChatRepository chatRepository;
    private final RedisManager redisManager;

    public Map<Long, TeamListDto> refreshRedisValue(final Chat chat, final Long teamId) {
        /*
         1. UserId + TeamId로 Key를 만들고 inRoom 값을 확인해 채팅방에 있는지 없는지 검사한다.
         2. inRoom = 1이면 채팅방 화면에 있는 것이니 메시지 값을 증가시키지 않는다.
         3. inRoom = 0이면 채팅방에서 나간 상태니까 메시지 값을 +1 증가시킨다.
         4. 수정된 unReadMessage 값을 기반으로 ZSet을 재정렬한다.
        */
        Map<Long, TeamListDto> results = new HashMap<>();
        List<Long> userIds = userTeamCheckService.findAllUserIdByTeamId(teamId);

        for (Long userId : userIds) {
            String inRoomKey = redisManager.getInRoomKey(userId, teamId);
            String unReadMessageKey = redisManager.getUnReadMessageKey(userId, teamId);
            String zSetKey = redisManager.getZSetKey(userId);

            int inRoom = redisManager.getInRoom(inRoomKey);
            if (inRoom == 0) {
                redisManager.updateUnReadMessage(unReadMessageKey);
            }
            redisManager.updateZSet(zSetKey, teamId, chat);
            results.put(userId, createTeamListDto(teamId, chat.getText(), chat.getCreatedAt(),
                    redisManager.getUnReadMessage(unReadMessageKey)));
        }

        return results;
    }

    @Transactional
    public Chat createChat(final ChatMessageRequestDto chatRequestDto, final Long teamId) {
        User user = userCheckService.findByUserId(chatRequestDto.getUserId());
        Team team = teamCheckService.findByTeamId(teamId);
        Chat chat = ChatConverter.toChat(chatRequestDto, user, team);
        return chatRepository.save(chat);
    }

    private TeamListDto createTeamListDto(final Long teamId, final String lastChat,
                                          final LocalDateTime lastChatTime, final int unReadMessage) {
        return TeamListDto.builder()
                .teamId(teamId)
                .teamName(teamCheckService.findNameByTeamId(teamId))
                .lastChat(lastChat)
                .lastChatTime(lastChatTime)
                .unReadMessage(unReadMessage)
                .numberOfUsers(userTeamCheckService.countNumOfUsersByTeamId(teamId))
                .build();
    }
}
