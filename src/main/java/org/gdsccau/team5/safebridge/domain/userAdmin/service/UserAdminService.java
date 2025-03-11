package org.gdsccau.team5.safebridge.domain.userAdmin.service;

import jakarta.transaction.Transactional;
import java.util.List;
import org.gdsccau.team5.safebridge.common.code.error.UserAdminErrorCode;
import org.gdsccau.team5.safebridge.common.exception.handler.ExceptionHandler;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.enums.Role;
import org.gdsccau.team5.safebridge.domain.user.repository.UserRepository;
import org.gdsccau.team5.safebridge.domain.userAdmin.dto.EmployeeResponseDto;
import org.gdsccau.team5.safebridge.domain.userAdmin.dto.EmployeeResponseDto.ListDto.EmployeeDto;
import org.gdsccau.team5.safebridge.domain.userAdmin.dto.UserAdminRequestDto.CreateDto;
import org.gdsccau.team5.safebridge.domain.userAdmin.dto.UserAdminResponseDto;
import org.gdsccau.team5.safebridge.domain.userAdmin.entity.UserAdmin;
import org.gdsccau.team5.safebridge.domain.userAdmin.repository.UserAdminRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserAdminService {

  private final UserRepository userRepository;
  private final UserAdminRepository userAdminRepository;

  public UserAdminService(UserRepository userRepository,
      UserAdminRepository userAdminRepository) {
    this.userRepository = userRepository;
    this.userAdminRepository = userAdminRepository;
  }

  /**
   * 관리자 근로자 추가
   *
   * @param adminId   관리자 Id
   * @param createDto 근로자 Id, 일용직 여부
   * @return 근로자 이름, 근로자 Id
   */
  public UserAdminResponseDto.CreateDto createUserAdmin(final Long adminId,
      final CreateDto createDto) {
    User admin = userRepository.findByIdAndRole(adminId, Role.ADMIN)
        .orElseThrow(() -> new ExceptionHandler(UserAdminErrorCode.ADMIN_NOT_FOUND));

    User employee = userRepository.findByIdAndRole(createDto.userId(), Role.MEMBER)
        .orElseThrow(() -> new ExceptionHandler(UserAdminErrorCode.ADMIN_NOT_FOUND));

    if (userAdminRepository.existsByAdminAndEmployee(admin, employee)) {
      throw new ExceptionHandler(UserAdminErrorCode.USER_ADIMIN_ALREADY_EXIST);
    }

    this.userAdminRepository.save(UserAdmin.builder().admin(admin).employee(employee)
        .isTemporaryWorker(createDto.isTemporaryWorker()).build());

    return UserAdminResponseDto.CreateDto.builder().userId(employee.getId())
        .name(employee.getName()).build();
  }

  /**
   * 근로자 리스트 메서드
   * @param adminId 관리자 아이디
   * @return 근로자 리스트 dto
   */
  public EmployeeResponseDto.ListDto getList(final Long adminId) {
    User admin = userRepository.findByIdAndRole(adminId, Role.ADMIN).orElseThrow(() ->
        new ExceptionHandler(UserAdminErrorCode.ADMIN_NOT_FOUND)
    );
    List<EmployeeDto> listDto = userAdminRepository.findByAdmin_Id(admin.getId())
        .stream()
        .map(userAdmin ->
            EmployeeDto.builder()
                .userId(userAdmin.getEmployee().getId())
                .name(userAdmin.getEmployee().getName())
                .build()
        ).toList();

    return EmployeeResponseDto.ListDto.builder().employees(listDto).employeeNumber(listDto.size())
        .adminId(adminId).build();
  }
}
