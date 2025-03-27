package org.gdsccau.team5.safebridge.domain.term.subscriber;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final RedisManager redisManager;

    @Async("threadPoolTaskExecutor")
    @Override
    public void onMessage(Message message, byte[] pattern) {
        Map<String, Double> hotTermMap = calculateHotTerm();

        // TODO Local Cache에 Hot Term을 저장한다.
    }

    private Map<String, Double> calculateHotTerm() {
        LocalDateTime currentTime = LocalDateTime.now();
        Map<String, Double> totalScoreMap = initTotalScoreMap(currentTime);
        calculateTermFindCount(totalScoreMap, currentTime);
        return totalScoreMap;
    }

    private Map<String, Double> getTermFindTimeMemberWithScore(final LocalDateTime currentTime) {
        Map<String, Double> map = new HashMap<>();
        redisManager.getTermFindTimeZSet(currentTime).forEach(tuple -> {
            String member = tuple.getValue();
            Double score = tuple.getScore();
            map.put(member, score);
        });
        return map;
    }

    private Map<String, Double> initTotalScoreMap(final LocalDateTime currentTime) {
        double findTimeWeight = 0.01; // TODO 몇으로 해야할까?
        Map<String, Double> termFindTimeMap = getTermFindTimeMemberWithScore(currentTime);
        Map<String, Double> totalScoreMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : termFindTimeMap.entrySet()) {
            String field = entry.getKey();
            Double lastFindTimeScore = entry.getValue();
            totalScoreMap.putIfAbsent(field, lastFindTimeScore * findTimeWeight);
        }
        return totalScoreMap;
    }

    private void calculateTermFindCount(Map<String, Double> totalScoreMap, final LocalDateTime currentTime) {
        int a = 1;
        double r = 0.1;

        for (int i = 0; i < 24; i++) {
            LocalDateTime hourTime = currentTime.minusHours(i);
            Map<Object, Object> findCountHash = redisManager.getTermFindCount(hourTime);

            for (Map.Entry<Object, Object> entry : findCountHash.entrySet()) {
                String field = entry.getKey().toString();
                Integer count = (Integer) entry.getValue();
                Double countWeight = a * Math.pow(1 - r, i);
                Double findCountScore = countWeight * count;

                totalScoreMap.computeIfPresent(field, (k, v) -> v + findCountScore);
            }
        }
    }
}
