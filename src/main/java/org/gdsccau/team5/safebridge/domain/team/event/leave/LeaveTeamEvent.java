package org.gdsccau.team5.safebridge.domain.team.event.leave;

import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeaveTeamEvent {

    private Long userId;
    private Long teamId;
    private Supplier<Integer> dbLookUp;
}
