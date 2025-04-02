package org.gdsccau.team5.safebridge.domain.userTeam.repository;

import java.sql.PreparedStatement;
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

    @Transactional
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
