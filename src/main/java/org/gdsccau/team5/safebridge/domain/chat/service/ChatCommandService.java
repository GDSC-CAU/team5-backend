package org.gdsccau.team5.safebridge.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gdsccau.team5.safebridge.domain.chat.converter.ChatConverter;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.request.ChatRequestDto.ChatMessageRequestDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.gdsccau.team5.safebridge.domain.chat.repository.ChatRepository;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.team.service.TeamQueryService;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatCommandService {

    private final UserQueryService userQueryService;
    private final TeamQueryService teamQueryService;
    private final ChatRepository chatRepository;

    @Transactional
    public ChatDto.ChatDetailDto createChat(final ChatMessageRequestDto chatRequestDto, final Long teamId) {
        User user = userQueryService.findByUserId(chatRequestDto.getUserId());
        Team team = teamQueryService.findByTeamId(teamId);
        Chat chat = ChatConverter.toChat(chatRequestDto, user, team);
        chatRepository.save(chat);
        return ChatConverter.toChatDetailDto(chat);
    }
}
