package org.gdsccau.team5.safebridge.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
  ADMIN(0, "관리자"),
  MEMBER(1, "근로자");

  private Integer level;
  private String description;
  }
