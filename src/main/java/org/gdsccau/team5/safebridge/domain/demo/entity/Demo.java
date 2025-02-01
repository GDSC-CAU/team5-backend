package org.gdsccau.team5.safebridge.domain.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "demo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Demo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long Id;

  private String name;

  public Demo(String name) {
    this.name = name;
  }
}
