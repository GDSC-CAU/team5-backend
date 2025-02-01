package org.gdsccau.team5.safebridge.domain.demo.dto;

import org.gdsccau.team5.safebridge.domain.demo.entity.Demo;

public record DemoDto(
    Long Id,
    String name
) {
  public static DemoDto fromEntity(Demo demo) {
    return new DemoDto(demo.getId(), demo.getName());
  }
}
