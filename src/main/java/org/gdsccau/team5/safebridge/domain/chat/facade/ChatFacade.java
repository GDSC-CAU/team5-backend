package org.gdsccau.team5.safebridge.domain.chat.facade;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.domain.chat.converter.ChatConverter;
import org.gdsccau.team5.safebridge.domain.chat.dto.request.ChatRequestDto.ChatMessageRequestDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatCheckService;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatService;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamListDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatFacade {

    private static final String CHAT_SUB_URL = "/sub/chats/";
    private static final String TEAMS_SUB_URL = "/sub/teams/";

    private final ChatService chatService;
    private final ChatCheckService chatCheckService;
    private final SimpMessagingTemplate messagingTemplate;

    public void chat(final ChatMessageRequestDto chatRequestDto, final Long teamId) {
        Chat chat = chatService.createChat(chatRequestDto, teamId);

        messagingTemplate.convertAndSend(CHAT_SUB_URL + teamId,
                ChatConverter.toChatResponseDto(chatRequestDto.getName(), chat));

        Map<Long, TeamListDto> map = chatService.refreshRedisValue(chat, teamId);

        for (Long userId : map.keySet()) {
            messagingTemplate.convertAndSend(TEAMS_SUB_URL + userId, map.get(userId));
        }
    }

    public Map<String, Object> findAllChats(final Long teamId, final Long userId, final Long cursorId) {
        return chatCheckService.findAllChatsByTeamId(teamId, userId, cursorId);
    }
}
