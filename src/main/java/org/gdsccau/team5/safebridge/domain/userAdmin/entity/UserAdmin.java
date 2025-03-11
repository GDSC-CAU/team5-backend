package org.gdsccau.team5.safebridge.domain.userAdmin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import org.gdsccau.team5.safebridge.domain.BaseEntity;
import org.gdsccau.team5.safebridge.domain.user.entity.User;

@Entity
@Builder
public class UserAdmin extends BaseEntity {

  @Id
  @Column(name = "user_admin_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_user_id")
  private User admin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_user_id")
  private User employee;

  private boolean isTemporaryWorker;
}
