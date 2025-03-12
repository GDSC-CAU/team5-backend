package org.gdsccau.team5.safebridge.domain.userAdmin.repository;

import java.util.List;
import java.util.Optional;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.userAdmin.entity.UserAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAdminRepository extends JpaRepository<UserAdmin, Long> {
  public interface UserAdminId {
    Long getId();
  }
  boolean existsByAdminAndEmployee(User admin, User employee);

  List<UserAdmin> findByAdmin_Id(Long adminId);

  List<UserAdminId> findAllByAdmin_IdAndIsTemporaryWorkerTrue(Long adminId);

  Optional<UserAdminId> findByAdmin_IdAndEmployeeId(Long adminId, Long employeeId);

  Optional<UserAdmin> findFirstByEmployee(User user);
}
