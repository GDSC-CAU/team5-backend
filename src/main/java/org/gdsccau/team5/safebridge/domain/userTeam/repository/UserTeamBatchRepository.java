package org.gdsccau.team5.safebridge.domain.userTeam.repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.domain.userTeam.dto.UserTeamDto.UserTeamUnReadMessageDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class UserTeamBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public void userTeamBatchInsert(final List<Long> userIds, final Long teamId) {
        String sql = "INSERT INTO user_team (in_room, access_date, un_read_message, user_id, team_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql,
                userIds,
                userIds.size(),
                (PreparedStatement ps, Long userId) -> {
                    ps.setInt(1, 0); // inRoom
                    ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now())); // accessDate
                    ps.setInt(3, 0); // unReadMessage
                    ps.setLong(4, userId); // userId
                    ps.setLong(5, teamId); // teamId
                });
    }

    public void unReadMessageBatchUpdate(final List<UserTeamUnReadMessageDto> dtos) {
        String sql = "UPDATE user_team SET un_read_message = ? WHERE user_id = ? AND team_id = ?";
        jdbcTemplate.batchUpdate(sql,
                dtos,
                dtos.size(),
                (PreparedStatement ps, UserTeamUnReadMessageDto dto) -> {
                    ps.setInt(1, dto.getUnReadMessage()); // unReadMessage
                    ps.setLong(2, dto.getUserId()); // WHERE 절
                    ps.setLong(3, dto.getTeamId()); // WHERE 절
                });
        // TODO 배치 크기를 size()가 아니라 숫자로 조절해서 배치 업데이트를 여러 번 하는게 나을까?
    }
}
