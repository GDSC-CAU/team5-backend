package org.gdsccau.team5.safebridge.domain.chat.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.code.success.ChatSuccessCode;
import org.gdsccau.team5.safebridge.common.response.ApiResponse;
import org.gdsccau.team5.safebridge.domain.chat.dto.request.ChatRequestDto.ChatMessageRequestDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.facade.ChatFacade;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatFacade chatFacade;

    @MessageMapping("/chats/teams/{teamId}")
    public void chat(final ChatMessageRequestDto chatRequestDto, @DestinationVariable final Long teamId) {
        Chat chat = chatFacade.createChat(chatRequestDto, teamId);
        chatFacade.chat(chatRequestDto, teamId, chat);
    }

    @GetMapping("/chats/teams/{teamId}")
    public ApiResponse<Map<String, Object>> getMessages(
            @PathVariable(name = "teamId") final Long teamId,
            @RequestParam(name = "userId") final Long userId,
            @RequestParam(name = "cursorId", required = false) final Long cursorId) {
        return ApiResponse.onSuccess(ChatSuccessCode.FIND_CHAT_IN_TEAM,
                chatFacade.findAllChats(cursorId, userId, teamId));
    }
}
