package org.gdsccau.team5.safebridge.domain.term.subscriber;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class RedisMessageSubscriber implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // TODO Redis에서 데이터를 꺼내 Hot Term을 계산하고 Local Cache에 저장한다.
    }
}
