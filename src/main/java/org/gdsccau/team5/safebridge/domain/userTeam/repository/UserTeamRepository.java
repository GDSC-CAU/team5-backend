package org.gdsccau.team5.safebridge.domain.userTeam.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.gdsccau.team5.safebridge.domain.team.dto.TeamDto.TeamOrderDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserDto.UserIdAndLanguageDto;
import org.gdsccau.team5.safebridge.domain.userTeam.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {

    @Query("SELECT ut.user.id FROM UserTeam ut WHERE ut.team.id = :teamId")
    List<Long> findAllUserIdByTeamId(@Param("teamId") final Long teamId);

    @Query("SELECT DISTINCT (ut.user.id) FROM UserTeam ut")
    List<Long> findAllUserIdWithTeam();

    @Query("SELECT new org.gdsccau.team5.safebridge.domain.user.dto.UserDto$UserIdAndLanguageDto(ut.user.id, ut.user.language) "
            + "FROM UserTeam ut "
            + "WHERE ut.team.id = :teamId")
    List<UserIdAndLanguageDto> findAllUserIdAndLanguageByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT count(*) FROM UserTeam ut WHERE ut.team.id = :teamId")
    Integer countNumOfUsersByTeamId(@Param("teamId") final Long teamId);

    @Query("SELECT ut.team.id FROM UserTeam ut WHERE ut.user.id = :userId")
    List<Long> findAllTeamIdByUserId(@Param("userId") final Long userId);

    @Query("SELECT ut FROM UserTeam ut WHERE ut.user.id = :userId AND ut.team.id = :teamId")
    Optional<UserTeam> findUserTeamByUserIdAndTeamId(@Param("userId") final Long userId,
                                                     @Param("teamId") final Long teamId);

    @Query("SELECT ut.accessDate FROM UserTeam ut WHERE ut.user.id = :userId AND ut.team.id = :teamId")
    Optional<LocalDateTime> findAccessDateByUserIdAndTeamId(@Param("userId") final Long userId,
                                                            @Param("teamId") final Long teamId);

    @Query("SELECT ut.inRoom FROM UserTeam ut WHERE ut.user.id = :userId AND ut.team.id = :teamId")
    Optional<Integer> findInRoomByUserIdAndTeamId(@Param("userId") final Long userId,
                                                  @Param("teamId") final Long teamId);

    @Query("SELECT ut.unReadMessage FROM UserTeam ut WHERE ut.user.id = :userId AND ut.team.id = :teamId")
    Optional<Integer> findUnReadMessageByUserIdAndTeamId(@Param("userId") final Long userId,
                                                         @Param("teamId") final Long teamId);

    @Query("SELECT new org.gdsccau.team5.safebridge.domain.team.dto.TeamDto$TeamOrderDto(ut.user.id, ut.team.id, h.lastChatTime) "
            + "FROM UserTeam ut "
            + "LEFT JOIN ( "
            + "     SELECT c.team.id AS teamId, MAX(c.createdAt) AS lastChatTime "
            + "     FROM Chat c "
            + "     GROUP BY c.team.id"
            + ") h ON ut.team.id = h.teamId"
    )
    List<TeamOrderDto> findAllTeamOrderByLastChatTime();
}
