package org.gdsccau.team5.safebridge.domain.user_team.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.gdsccau.team5.safebridge.domain.user_team.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {

    @Query("SELECT ut.user.id FROM UserTeam ut WHERE ut.team.id = :teamId")
    List<Long> findAllUserIdByTeamId(final Long teamId);

    @Query("SELECT count(*) FROM UserTeam ut WHERE ut.team.id = :teamId")
    int countNumOfUsersByTeamId(final Long teamId);

    @Query("SELECT ut.team.id FROM UserTeam ut WHERE ut.user.id = :userId")
    List<Long> findAllTeamIdByUserId(final Long userId);

    @Query("SELECT ut FROM UserTeam ut WHERE ut.user.id = :userId AND ut.team.id = :teamId")
    Optional<UserTeam> findUserTeamByUserIdAndTeamId(@Param("userId") final Long userId,
                                                     @Param("teamId") final Long teamId);

    @Query("SELECT ut.accessDate FROM UserTeam ut WHERE ut.user.id = :userId AND ut.team.id = :teamId")
    Optional<LocalDateTime> findAccessDateByUserIdAndTeamId(@Param("userId") final Long userId,
                                                            @Param("teamId") final Long teamId);

}
