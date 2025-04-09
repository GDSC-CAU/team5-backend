package org.gdsccau.team5.safebridge.domain.team.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TeamLeaveEvent extends ApplicationEvent {

    private Long userId;
    private Long teamId;

    public TeamLeaveEvent(final Object source, final Long userId, final Long teamId) {
        super(source);
        this.userId = userId;
        this.teamId = teamId;
    }
}
