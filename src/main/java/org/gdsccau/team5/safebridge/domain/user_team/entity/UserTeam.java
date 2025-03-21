package org.gdsccau.team5.safebridge.domain.user_team.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.gdsccau.team5.safebridge.domain.BaseEntity;
import org.gdsccau.team5.safebridge.domain.team.entity.Team;
import org.gdsccau.team5.safebridge.domain.user.entity.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class UserTeam extends BaseEntity {

    @Id
    @Column(name = "user_team_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime accessDate;
    private int unReadMessage;
    private int inRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public void updateAccessDate() {
        this.accessDate = LocalDateTime.now();
    }

    public void updateInRoomWhenJoin() {
        this.inRoom = 1;
    }

    public void updateInRoomWhenLeave() {
        this.inRoom = 0;
    }
}
