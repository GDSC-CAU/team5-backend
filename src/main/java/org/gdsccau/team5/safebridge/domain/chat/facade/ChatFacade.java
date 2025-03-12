package org.gdsccau.team5.safebridge.domain.chat.facade;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.common.term.TermManager;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.TermDataWithNewChatDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.request.ChatRequestDto.ChatMessageRequestDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.ChatMessageWithIsReadResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.WorkResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatCheckService;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatSendService;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatService;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.team.service.TeamCheckService;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.enums.Role;
import org.gdsccau.team5.safebridge.domain.user.service.UserCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class ChatFacade {

    private final ChatService chatService;
    private final ChatSendService chatSendService;
    private final ChatCheckService chatCheckService;
    private final TeamCheckService teamCheckService;
    private final UserCheckService userCheckService;
    private final UserTeamCheckService userTeamCheckService;
    private final TermManager termManager;

    public void chat(final ChatMessageRequestDto chatRequestDto, final Long teamId, final Chat chat) {
        TermDataWithNewChatDto result = termManager.query(chatRequestDto.getMessage());
        chatSendService.sendChatMessage(result, chatRequestDto.getName(), chat, teamId);
        List<Long> userIds = userTeamCheckService.findAllUserIdByTeamId(teamId);
        for (Long userId : userIds) {
            Language language = userCheckService.findLanguageByUserId(userId);
            chatSendService.sendTranslatedMessage(result, language, chat, teamId, userId);
            chatSendService.sendTeamData(chat, teamId, userId);
        }
    }

    @Transactional
    public Chat createChat(final ChatMessageRequestDto chatRequestDto, final Long teamId) {
        User user = userCheckService.findByUserId(chatRequestDto.getUserId());
        Team team = teamCheckService.findByTeamId(teamId);
        return chatService.createChat(chatRequestDto, user, team);
    }

    public Map<String, Object> findAllChats(final String role, final Long cursorId, final Long userId,
                                            final Long teamId) {
        Language language = userCheckService.findLanguageByUserId(userId);
        Slice<ChatMessageWithIsReadResponseDto> chatSlice = chatCheckService.findAllChatsByTeamId(Role.valueOf(role),
                cursorId,
                teamId, language);
        LocalDateTime accessDate = userTeamCheckService.findAccessDateByUserIdAndTeamId(userId, teamId);
        for (ChatMessageWithIsReadResponseDto chatMessage : chatSlice.getContent()) {
            chatMessage.setRead(chatMessage.getSendTime().isBefore(accessDate));
        }
        Map<String, Object> response = new HashMap<>();
        response.put("messages", chatSlice.getContent());
        response.put("hasNext", chatSlice.hasNext());
        return response;
    }

    public List<WorkResponseDto> findAllWorks(final Long userId) {
        return chatCheckService.findAllWorks(userId);
    }
}
