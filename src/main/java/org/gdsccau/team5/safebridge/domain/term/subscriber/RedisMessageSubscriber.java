package org.gdsccau.team5.safebridge.domain.term.subscriber;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.gdsccau.team5.safebridge.common.redis.RedisManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final RedisManager redisManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // TODO Redis에서 데이터를 꺼내 Hot Term을 계산하고 Local Cache에 저장한다.
        LocalDateTime currentTime = LocalDateTime.now();

        // 메소드 호출 날짜 ~ 어제 날짜 사이의 모든 "word:language"가 최근 순서로 저장되어 있다.
        Set<String> termFindTimeZSet = redisManager.getTermFindTimeZSet(currentTime);

        // 메소드 호출 날짜 ~ 어제 날짜 사이의 시간별 (24구간)에 대한 "word:language"의 호출 횟수
        for (int i = 0; i < 24; i++) {
            LocalDateTime hourTime = currentTime.minusHours(i);
            Map<Object, Object> findCountHash = redisManager.getTermFindCount(hourTime);
            findCountHash.forEach((k, v) -> {
                String field = (String) k;
                String word = field.split(":")[0];
                String language = field.split(":")[1];
                Integer count = Integer.parseInt(((String) v));

                
            });
        }
    }
}
