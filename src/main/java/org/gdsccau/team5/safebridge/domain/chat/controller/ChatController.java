package org.gdsccau.team5.safebridge.domain.chat.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.domain.chat.converter.ChatConverter;
import org.gdsccau.team5.safebridge.domain.chat.dto.request.ChatRequestDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatCheckService;
import org.gdsccau.team5.safebridge.domain.chat.service.ChatService;
import org.gdsccau.team5.safebridge.domain.team.dto.response.TeamResponseDto.TeamListDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final ChatCheckService chatCheckService;

    private static final String MESSAGE_SUB_URL = "/sub/chat/";
    private static final String TEAMLIST_SUB_URL = "/sub/teams/";

    /**
     * WebSocketStompBrokerConfig에서 Handler대신 Controller를 통해 메시지 전달을 관리할 수 있다.
     *
     * @MessageMapping은 STOMP 웹소켓 통신을 통해 전달된 메시지의 destination 헤더와 경로가 일치한 핸들러가 메시지를 처리한다. Config에서 설정한 prefix인 "/pub"과
     * 합쳐진 "/pub/chat"라는 destination 헤더를 가진 메시지가 이 핸들러로 처리된다.
     * @SendTo는 핸들러에서 처리를 마친 후 메시지를 "/sub/chat"를 구독한 구독자에게 보내준다.
     */
    @MessageMapping("/chat/{teamId}")
    public void chat(final ChatRequestDto.ChatMessageRequestDto chatRequestDto,
                     @DestinationVariable final Long teamId) {

        // 1. Chat 엔티티 저장하기 (user, team 같이 저장해야)
        Chat chat = chatService.createChat(chatRequestDto);

        // 2. 실시간 메시지 전송하기 (번역 처리 필요)
        messagingTemplate.convertAndSend(MESSAGE_SUB_URL + teamId,
                ChatConverter.toChatResponseDto(chatRequestDto.getName(), chat));

        // 3. Redis 관리하기, 업데이트된 채팅방 리스트 데이터 가져오기
        Map<Long, TeamListDto> map = chatService.refreshRedisValue(chat, teamId);

        // 4. 업데이트된 채팅방 리스트 데이터 전송하기
        for (Long userId : map.keySet()) {
            messagingTemplate.convertAndSend(TEAMLIST_SUB_URL + userId, map.get(userId));
        }
    }

    /**
     * 채팅방 메시지 내역 받아오는 API 여기서 고려해야할 점은 안 읽은 메시지를 체크해서 넘겨줘야 그 메시지부터 화면에 보이게 할 수 있을 듯 안 읽은 메시지와 읽은 메시지를 둘다 넘겨주고 어차피 시간
     * 순서니까 최초 안 읽은 메시지부터 화면에 보이게
     */
//    @GetMapping("/chat")
//    public ApiResponse<List<ChatResponseDto.ChatMessageResponseDto>> getMessages(final Long teamId) {
//        return ApiResponse.onSuccess(ChatSuccessCode.FIND_CHAT_IN_TEAM, chatCheckService.findChatsByTeamId(teamId));
//    }
}
