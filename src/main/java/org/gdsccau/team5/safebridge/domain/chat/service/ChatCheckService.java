package org.gdsccau.team5.safebridge.domain.chat.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.ChatMetaDataDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.ChatMessageWithIsReadResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.repository.ChatCustomRepository;
import org.gdsccau.team5.safebridge.domain.chat.repository.ChatRepository;
import org.gdsccau.team5.safebridge.domain.user_team.repository.UserTeamRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatCheckService {

    private final ChatRepository chatRepository;
    private final ChatCustomRepository chatCustomRepository;
    private final UserTeamRepository userTeamRepository;

    @Transactional(readOnly = true)
    public Chat findChatById(final Long chatId) {
        return chatRepository.findById(chatId).orElse(null);
    }

    @Transactional(readOnly = true)
    public ChatMetaDataDto findChatMetaDataByTeamId(final Long teamId) {
        Pageable pageable = PageRequest.of(0, 1);
        Optional<ChatMetaDataDto> chatMetaDataDto = chatRepository.findChatMetaDataDtoByTeamId(teamId, pageable)
                .getContent().stream().findFirst();
        return chatMetaDataDto.orElseGet(() -> ChatMetaDataDto.builder()
                .lastChat(null)
                .lastChatTime(null)
                .build());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findAllChatsByTeamId(final Long cursorId, final Long userId, final Long teamId) {
        Pageable pageable = PageRequest.of(0, 5);
        Slice<ChatMessageWithIsReadResponseDto> chatSlice = chatCustomRepository.findAllChatsByTeamId(
                cursorId, teamId, pageable);
        // 순환참조 발생!!!!
        LocalDateTime accessDate = userTeamRepository.findAccessDateByUserIdAndTeamId(userId, teamId).orElse(null);
        for (ChatMessageWithIsReadResponseDto chatMessage : chatSlice.getContent()) {
            chatMessage.setRead(chatMessage.getSendTime().isBefore(accessDate));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("messages", chatSlice.getContent());
        response.put("hasNext", chatSlice.hasNext());
        return response;
    }
}
