package org.gdsccau.team5.safebridge.domain.user.repository;

import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.name = :username")
    User findByUsername(final String username);
}
