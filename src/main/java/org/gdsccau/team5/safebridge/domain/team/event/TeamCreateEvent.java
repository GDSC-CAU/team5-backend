package org.gdsccau.team5.safebridge.domain.team.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class TeamCreateEvent extends ApplicationEvent {

    private List<Long> userIds;
    private Long teamId;

    public TeamCreateEvent(final Object source, final List<Long> userIds, final Long teamId) {
        super(source);
        this.userIds = userIds;
        this.teamId = teamId;
    }
}
