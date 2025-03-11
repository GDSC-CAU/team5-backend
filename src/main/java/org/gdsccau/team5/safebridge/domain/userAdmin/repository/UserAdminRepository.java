package org.gdsccau.team5.safebridge.domain.userAdmin.repository;

import java.util.List;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.userAdmin.entity.UserAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAdminRepository extends JpaRepository<UserAdmin, Long> {

  boolean existsByAdminAndEmployee(User admin, User employee);

  List<UserAdmin> findByAdmin_Id(Long adminId);
}
