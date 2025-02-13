package org.gdsccau.team5.safebridge.domain.user_team.repository;

import java.util.List;
import org.gdsccau.team5.safebridge.domain.user_team.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {

    @Query("SELECT ut.user.id FROM UserTeam ut WHERE ut.team.id = :teamId")
    List<Long> findAllUserIdByTeamId(final Long teamId);
}
