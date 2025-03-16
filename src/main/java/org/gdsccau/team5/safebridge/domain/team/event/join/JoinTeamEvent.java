package org.gdsccau.team5.safebridge.domain.team.event.join;

import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinTeamEvent {

    private Long userId;
    private Long teamId;
    private Supplier<Integer> dbLookUp;
}
