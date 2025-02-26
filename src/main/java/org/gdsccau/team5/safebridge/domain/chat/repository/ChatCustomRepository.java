package org.gdsccau.team5.safebridge.domain.chat.repository;

import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.ChatMessageWithIsReadResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatCustomRepository {

    Slice<ChatMessageWithIsReadResponseDto> findAllChatsByTeamId(final Long cursorId, final Long teamId,
                                                                 final Pageable pageable);
}
