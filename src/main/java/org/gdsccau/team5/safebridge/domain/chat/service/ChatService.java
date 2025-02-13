package org.gdsccau.team5.safebridge.domain.chat.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.chat.converter.ChatConverter;
import org.gdsccau.team5.safebridge.domain.chat.dto.request.ChatRequestDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.repository.ChatRepository;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamListDto;
import org.gdsccau.team5.safebridge.domain.team.service.TeamCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final UserTeamCheckService userTeamCheckService;
    private final TeamCheckService teamCheckService;
    private final ChatRepository chatRepository;
    private final RedisTemplate<String, String> redisTemplate;

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
            if (updateInRoom("userId:" + userId + "teamId:" + teamId + "inRoom") == 1) {
                continue;
            }
            int unReadMessage = updateUnReadMessage("userId:" + userId + "teamId:" + teamId + "unReadMessage");
            updateZSet("userId:" + userId + "Team", teamId, chat, unReadMessage);

            results.put(userId, createTeamListDto(teamId, chat.getText(), chat.getCreatedAt()));
        }

        return results;
    }

    @Transactional
    public Chat createChat(final ChatRequestDto.ChatMessageRequestDto chatRequestDto) {
        Chat chat = ChatConverter.toChat(chatRequestDto);
        return chatRepository.save(chat);
    }

    private int updateInRoom(final String inRoomKey) {
        String inRoomValue = redisTemplate.opsForValue().get(inRoomKey);
        return inRoomValue != null ? Integer.parseInt(inRoomValue) : 0;
    }

    private int updateUnReadMessage(final String unReadMessageKey) {
        redisTemplate.opsForValue().increment(unReadMessageKey, 1);
        String unReadMessageValue = redisTemplate.opsForValue().get(unReadMessageKey);
        return unReadMessageValue != null ? Integer.parseInt(unReadMessageValue) : 0;
    }

    private void updateZSet(final String zSetKey, final Long teamId, final Chat chat, final int unReadMessage) {
        long score = chat.getCreatedAt()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant()
                .toEpochMilli() + (unReadMessage * 1000L);
        redisTemplate.opsForZSet().add(zSetKey, String.valueOf(teamId), score);
    }

    private TeamListDto createTeamListDto(final Long teamId, final String lastChat,
                                          final LocalDateTime lastChatTime) {
        return TeamListDto.builder()
                .teamId(teamId)
                .teamName(teamCheckService.findNameByTeamId(teamId))
                .lastChat(lastChat)
                .lastChatTime(lastChatTime)
                .build();
    }
}
