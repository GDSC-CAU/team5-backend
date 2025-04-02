package org.gdsccau.team5.safebridge.domain.userTeam.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.userTeam.converter.UserTeamConverter;
import org.gdsccau.team5.safebridge.domain.userTeam.dto.UserTeamDto.UserTeamUnReadMessageDto;
import org.gdsccau.team5.safebridge.domain.userTeam.service.UserTeamQueryService;
import org.gdsccau.team5.safebridge.domain.userTeam.service.UserTeamCommandService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnReadMessageBatchScheduler {

    private final UserTeamCommandService userTeamCommandService;
    private final RedisManager redisManager;
    private final UserTeamQueryService userTeamQueryService;

    @Scheduled(fixedRate = 300000) // 5분
    public void syncUnReadMessageToDB() {
        List<UserTeamUnReadMessageDto> dtos = new ArrayList<>();
        Set<String> unReadMessageDirtySet = redisManager.getUnReadMessageDirtySet();
        if (!unReadMessageDirtySet.isEmpty()) {
            unReadMessageDirtySet.forEach(data -> {
                Long userId = Long.parseLong(data.split(":")[0]);
                Long teamId = Long.parseLong(data.split(":")[1]);
                int unReadMessage = redisManager.getUnReadMessageOrDefault(redisManager.getUnReadMessageKey(userId, teamId),
                        () -> userTeamQueryService.findUnReadMessageByUserIdAndTeamId(userId, teamId));
                dtos.add(UserTeamConverter.toUserTeamUnReadMessageDto(userId, teamId, unReadMessage));
            });
            userTeamCommandService.syncUnReadMessageToDB(dtos);
            redisManager.clearUnReadMessageDirtySet(unReadMessageDirtySet);
        }
    }

    private List<String> getAllUnReadMessageKeys() {
        return redisManager.getUnReadMessageDirtySet().stream()
                .map(data -> {
                    Long userId = Long.parseLong(data.split(":")[0]);
                    Long teamId = Long.parseLong(data.split(":")[1]);
                    return redisManager.getUnReadMessageKey(userId, teamId);
                }).toList();
    }
}
