package org.gdsccau.team5.safebridge.domain.chat.repository;

import java.util.List;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.ChatMetaDataDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.WorkResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT new org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto$ChatMetaDataDto(c.text, c.createdAt) "
            + "FROM Chat c "
            + "WHERE c.team.id = :teamId "
            + "ORDER BY c.createdAt DESC")
    Page<ChatMetaDataDto> findChatMetaDataDtoByTeamId(@Param("teamId") final Long teamId, final Pageable pageable);

    @Query("SELECT new org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto$WorkResponseDto(c.id, c.team.id, c.text) "
            + "FROM Chat c "
            + "WHERE c.team.id IN :teamIds AND c.isTodo is true "
            + "ORDER BY c.createdAt DESC")
    List<WorkResponseDto> findAllWorksByTeamIds(@Param("teamIds") final List<Long> teamIds);
}
