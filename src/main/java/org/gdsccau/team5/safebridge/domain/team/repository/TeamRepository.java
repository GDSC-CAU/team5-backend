package org.gdsccau.team5.safebridge.domain.team.repository;

import java.util.Optional;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT t.name FROM Team t WHERE t.id = :teamId")
    Optional<String> findNameByTeamId(final Long teamId);
}
