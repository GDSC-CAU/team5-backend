package org.gdsccau.team5.safebridge.domain.user.repository;

import java.util.List;
import java.util.Optional;
import org.gdsccau.team5.safebridge.common.term.Language;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.enums.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(final String loginId);

    Boolean existsByLoginId(final String loginId);

    @Query("SELECT u.language FROM User u WHERE u.id = :userId")
    Optional<Language> findLanguageByUserId(final Long userId);

    @NotNull
    List<User> findByRole(Role role);

    Optional<User> findByIdAndRole(Long Id, Role role);
    Optional<User> findByLoginIdAndRole(String loginId, Role role);
}
