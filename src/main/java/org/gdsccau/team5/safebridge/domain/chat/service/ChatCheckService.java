package org.gdsccau.team5.safebridge.domain.chat.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.common.code.error.ChatErrorCode;
import org.gdsccau.team5.safebridge.common.exception.handler.ExceptionHandler;
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
        Chat chat = chatRepository.findById(chatId).orElse(null);
        this.validateChatData(chat);
        return chat;
    }

    @Transactional(readOnly = true)
    public ChatMetaDataDto findChatMetaDataByTeamId(final Long teamId) {
        Pageable pageable = PageRequest.of(0, 1);
        Optional<ChatMetaDataDto> chatMetaDataDto = chatRepository.findChatMetaDataDtoByTeamId(teamId, pageable)
                .getContent().stream().findFirst();
        this.validateChatData(chatMetaDataDto.orElse(null));
        return chatMetaDataDto.orElseGet(() -> ChatMetaDataDto.builder()
                .lastChat(null)
                .lastChatTime(null)
                .build());
    }

    @Transactional(readOnly = true)
    public Slice<ChatMessageWithIsReadResponseDto> findAllChatsByTeamId(final Long cursorId, final Long teamId) {
        Pageable pageable = PageRequest.of(0, 5);
        Slice<ChatMessageWithIsReadResponseDto> dtos = chatCustomRepository.findAllChatsByTeamId(cursorId, teamId,
                pageable);
        this.validateChatData(dtos.getContent());
        return dtos;
    }

    private <T> void validateChatData(final T data) {
        if (data == null) {
            throw new ExceptionHandler(ChatErrorCode.CHAT_NOT_FOUND);
        }
    }
}
