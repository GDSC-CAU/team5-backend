package org.gdsccau.team5.safebridge.domain.user.service;

import jakarta.transaction.Transactional;
import org.gdsccau.team5.safebridge.common.code.error.AuthErrorCode;
import org.gdsccau.team5.safebridge.common.exception.handler.ExceptionHandler;
import org.gdsccau.team5.safebridge.domain.user.dto.UserRequestDto.AdminSignUpDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserRequestDto.LoginDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserRequestDto.UserSignUpDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserResponseDto;
import org.gdsccau.team5.safebridge.domain.user.dto.UserResponseDto.SignUpDto;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.gdsccau.team5.safebridge.domain.user.enums.Role;
import org.gdsccau.team5.safebridge.domain.user.repository.UserRepository;
import org.gdsccau.team5.safebridge.domain.user.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserAuthService {

  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserAuthService(
      JwtUtil jwtUtil,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder
  ) {
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * 로그인
   *
   * @param loginDto login Dto
   * @return response dto
   */
  public UserResponseDto.LoginDto login(final LoginDto loginDto) {
    User user = this.getUserByLoginId(loginDto.loginId());

    if (!this.checkUserPassword(user, loginDto.password())) {
      throw new ExceptionHandler(AuthErrorCode.LOGIN_FAILED);
    }

    String token = jwtUtil.getAccessToken(user.getId());

    return new UserResponseDto.LoginDto(user.getId(), user.getRole(), token);
  }

  /**
   * User 생성(회원가입) 로직
   *
   * @param userSignUpDto 회원가입 dto
   * @return dto
   */
  public SignUpDto signUpUser(final UserSignUpDto userSignUpDto) {

    this.validateLoginIdDuplication(userSignUpDto.loginId());

    User user = User.builder()
        .loginId(userSignUpDto.loginId())
        .name(userSignUpDto.name())
        .language(userSignUpDto.language())
        .role(Role.MEMBER)
        .password(passwordEncoder.encode(userSignUpDto.password()))
        .build();

    userRepository.save(user);

    String token = jwtUtil.getAccessToken(user.getId());

    return new UserResponseDto.SignUpDto(user.getId(), user.getRole(), token);
  }

  /**
   * 관리자 회원가입
   *
   * @param adminSignUpDto 관리자 회원가입 dto
   * @return dto
   */
  public UserResponseDto.SignUpDto signUpAdmin(final AdminSignUpDto adminSignUpDto) {
    this.validateLoginIdDuplication(adminSignUpDto.loginId());

    User user = User.builder()
        .loginId(adminSignUpDto.loginId())
        .name(adminSignUpDto.name())
        .role(Role.ADMIN)
        .password(passwordEncoder.encode(adminSignUpDto.password()))
        .build();

    userRepository.save(user);

    String token = jwtUtil.getAccessToken(user.getId());

    return new UserResponseDto.SignUpDto(user.getId(), user.getRole(), token);
  }

  /**
   * 로그인 아이디로 유저 찾는 로직
   *
   * @param loginId 로그인 아이디
   * @return user
   */
  private User getUserByLoginId(final String loginId) {
    return userRepository.findByLoginId(loginId)
        .orElseThrow(() -> new ExceptionHandler(AuthErrorCode.LOGIN_USER_NOT_FOUND));
  }

  /**
   * 유저 비밀번호 검증
   *
   * @param user     유저
   * @param password 비밀번호
   * @return 매치 여부
   */
  private boolean checkUserPassword(final User user, final String password) {

    return passwordEncoder.matches(password, user.getPassword());
  }

  /**
   * 로그인 아이디 중복 검사
   * @param loginId 로그인 아이디
   */
  private void validateLoginIdDuplication(final String loginId) {

    if (userRepository.existsByLoginId(loginId)) {
      throw new ExceptionHandler(AuthErrorCode.LOGIN_ID_ALREADY_EXIST);
    }
  }
}
