package org.gdsccau.team5.safebridge.domain.chat.repository;

import java.util.List;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.ChatDto.ChatMetaDataDto;
import org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto.ChatMessageWithIsReadResponseDto;
import org.gdsccau.team5.safebridge.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRepository extends JpaRepository<Chat, Long> {

//    @Query("SELECT new org.gdsccau.team5.safebridge.domain.chat.dto.response.ChatResponseDto$ChatMessageWithIsReadResponseDto(u.name, c.text, c.createdAt, "
//            + " CASE WHEN ut.accessDate < c.createdAt THEN false ELSE true END) "
//            + "FROM Chat c, User u, UserTeam ut "
//            + "WHERE c.team.id = :teamId AND c.user.id = u.id AND c.user.id = ut.user.id "
//            + "ORDER BY c.createdAt ASC")
//    List<ChatMessageWithIsReadResponseDto> findMessageByTeamId(final Long teamId);
}
