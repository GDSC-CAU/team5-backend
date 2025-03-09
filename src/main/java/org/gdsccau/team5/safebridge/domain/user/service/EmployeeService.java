package org.gdsccau.team5.safebridge.domain.user.service;

import java.util.List;
import org.gdsccau.team5.safebridge.domain.user.dto.EmployeeResponseDto;
import org.gdsccau.team5.safebridge.domain.user.dto.EmployeeResponseDto.ListDto;
import org.gdsccau.team5.safebridge.domain.user.dto.EmployeeResponseDto.ListDto.EmployeeDto;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.enums.Role;
import org.gdsccau.team5.safebridge.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

  private final UserRepository userRepository;

  public EmployeeService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public final EmployeeResponseDto.ListDto getList(final Long adminId) {
    List<EmployeeDto> listDto = this.userRepository.findByRole(Role.MEMBER)
        .stream()
        .map(user ->
            EmployeeDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .build()
        ).toList();

    return EmployeeResponseDto.ListDto.builder().employees(listDto).employeeNumber(listDto.size())
        .adminId(adminId).build();
  }
}
