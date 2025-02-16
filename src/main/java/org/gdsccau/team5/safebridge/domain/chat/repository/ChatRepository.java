package org.gdsccau.team5.safebridge.domain.chat.repository;

import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.ChatMetaDataDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT new org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto$ChatMetaDataDto(c.text, c.createdAt) "
            + "FROM Chat c "
            + "WHERE c.team.id = :teamId "
            + "ORDER BY c.createdAt DESC")
    Page<ChatMetaDataDto> findChatMetaDataDtoByTeamId(final Long teamId, final Pageable pageable);
}
