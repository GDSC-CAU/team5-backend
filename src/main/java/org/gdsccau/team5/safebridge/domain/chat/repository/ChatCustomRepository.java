package org.gdsccau.team5.safebridge.domain.chat.repository;

import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.ChatMessageWithIsReadResponseDto;
import org.gdsccau.team5.safebridge.domain.user.enums.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatCustomRepository {

    Slice<ChatMessageWithIsReadResponseDto> findAllChatsByTeamId(final Role role,
                                                                 final Long cursorId,
                                                                 final Long teamId,
                                                                 final Language language,
                                                                 final Pageable pageable);
}
