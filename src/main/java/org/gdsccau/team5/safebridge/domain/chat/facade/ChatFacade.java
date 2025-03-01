package org.gdsccau.team5.safebridge.domain.chat.facade;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.common.term.TermManager;
import org.gdsccau.team5.safebridge.domain.chat.converter.ChatConverter;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataWithNewChatDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.request.ChatRequestDto.ChatMessageRequestDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.ChatMessageWithIsReadResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatCheckService;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatService;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamListDto;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.team.service.TeamCheckService;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.service.UserCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatFacade {

    private static final String CHAT_SUB_URL = "/sub/chats/";
    private static final String TEAMS_SUB_URL = "/sub/teams/";

    private final ChatService chatService;
    private final ChatCheckService chatCheckService;
    private final TeamCheckService teamCheckService;
    private final UserCheckService userCheckService;
    private final UserTeamCheckService userTeamCheckService;

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisManager redisManager;
    private final TermManager termManager;

    public void chat(final ChatMessageRequestDto chatRequestDto, final Long teamId, final Chat chat) {
        TermDataWithNewChatDto result = termManager.query(chatRequestDto.getMessage());
        String translatedText = termManager.translate(result.getNewChat());
        messagingTemplate.convertAndSend(CHAT_SUB_URL + teamId,
                ChatConverter.toChatResponseDto(chatRequestDto.getName(), chat, result.getTerms(), translatedText));
        Map<Long, TeamListDto> map = this.refreshRedisValue(chat, teamId);
        for (Long userId : map.keySet()) {
            messagingTemplate.convertAndSend(TEAMS_SUB_URL + userId, map.get(userId));
        }
    }

    @Transactional
    public Chat createChat(final ChatMessageRequestDto chatRequestDto, final Long teamId) {
        User user = userCheckService.findByUserId(chatRequestDto.getUserId());
        Team team = teamCheckService.findByTeamId(teamId);
        return chatService.createChat(chatRequestDto, user, team);
    }

    public Map<String, Object> findAllChats(final Long cursorId, final Long userId, final Long teamId) {
        Slice<ChatMessageWithIsReadResponseDto> chatSlice = chatCheckService.findAllChatsByTeamId(cursorId, teamId);
        LocalDateTime accessDate = userTeamCheckService.findAccessDateByUserIdAndTeamId(userId, teamId);
        for (ChatMessageWithIsReadResponseDto chatMessage : chatSlice.getContent()) {
            chatMessage.setRead(chatMessage.getSendTime().isBefore(accessDate));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("messages", chatSlice.getContent());
        response.put("hasNext", chatSlice.hasNext());
        return response;
    }

    private Map<Long, TeamListDto> refreshRedisValue(final Chat chat, final Long teamId) {
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
            results.put(userId, createTeamListDto(
                    teamId, chat.getText(), chat.getCreatedAt(), redisManager.getUnReadMessage(unReadMessageKey)
            ));
        }

        return results;
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
