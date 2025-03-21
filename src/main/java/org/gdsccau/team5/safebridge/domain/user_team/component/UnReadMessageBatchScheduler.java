package org.gdsccau.team5.safebridge.domain.user_team.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.gdsccau.team5.safebridge.domain.user_team.converter.UserTeamConverter;
import org.gdsccau.team5.safebridge.domain.user_team.dto.UserTeamDto.UserTeamUnReadMessageDto;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamCheckService;
import org.gdsccau.team5.safebridge.domain.user_team.service.UserTeamService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnReadMessageBatchScheduler {

    private final UserTeamService userTeamService;
    private final RedisManager redisManager;
    private final UserTeamCheckService userTeamCheckService;

    @Scheduled(fixedRate = 300000) // 5분
    public void syncUnReadMessageToDB() {
        List<UserTeamUnReadMessageDto> dtos = new ArrayList<>();
        Set<String> updatedSet = redisManager.getUpdatedSet();
        if (!updatedSet.isEmpty()) {
            updatedSet.forEach(data -> {
                Long userId = Long.parseLong(data.split(":")[0]);
                Long teamId = Long.parseLong(data.split(":")[1]);
                int unReadMessage = redisManager.getUnReadMessage(redisManager.getUnReadMessageKey(userId, teamId),
                        () -> userTeamCheckService.findUnReadMessageByUserIdAndTeamId(userId, teamId));
                dtos.add(UserTeamConverter.toUserTeamUnReadMessageDto(userId, teamId, unReadMessage));
            });
            userTeamService.syncUnReadMessageToDB(dtos);
            redisManager.clearUpdatedSet(updatedSet);
        }
    }

    private List<String> getAllUnReadMessageKeys() {
        return redisManager.getUpdatedSet().stream()
                .map(data -> {
                    Long userId = Long.parseLong(data.split(":")[0]);
                    Long teamId = Long.parseLong(data.split(":")[1]);
                    return redisManager.getUnReadMessageKey(userId, teamId);
                }).toList();
    }
}
