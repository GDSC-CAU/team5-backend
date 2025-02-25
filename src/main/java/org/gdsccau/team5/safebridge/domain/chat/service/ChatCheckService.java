package org.gdsccau.team5.safebridge.domain.chat.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.ChatMetaDataDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.ChatMessageWithIsReadResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.repository.ChatCustomRepository;
import org.gdsccau.team5.safebridge.domain.chat.repository.ChatRepository;
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
    public Slice<ChatMessageWithIsReadResponseDto> findAllChatsByTeamId(final Long cursorId, final Long teamId) {
        Pageable pageable = PageRequest.of(0, 5);
        return chatCustomRepository.findAllChatsByTeamId(cursorId, teamId, pageable);
    }
}
