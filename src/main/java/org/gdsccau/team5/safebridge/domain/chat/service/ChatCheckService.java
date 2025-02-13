package org.gdsccau.team5.safebridge.domain.chat.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.repository.ChatRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatCheckService {

    private final ChatRepository chatRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    public Chat findChatById(final Long chatId) {
        return chatRepository.findById(chatId).orElse(null);
    }

//    읽은 메시지와 안 읽은 메시지를 구분하기, Pagination 찾아보기
//    @Transactional(readOnly = true)
//    public List<ChatResponseDto.ChatMessageResponseDto> findChatsByTeamId(final Long teamId) {
//        return chatRepository.findMessageByTeamId(teamId).stream()
//                .map(message -> ChatResponse);
//    }
}
