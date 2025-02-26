package org.gdsccau.team5.safebridge.domain.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompBrokerConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * "/sub" prefix가 붙은 url에 대해서는 메시지 브로커가 바로 구독자에게 메시지를 전달한다.
     * "/pub" prefix 기반 url은 메시지 브로커로 바로 전송되는 것이 아니라 메시지 핸들러로 전송된다. 서버에서 추가 가공처리를 위한 경로이다.
     * 이 때 브로커는 스프링 인메모리 브로커를 사용하기 때문에 분산환경에선 RabbitMQ나 Kafka 같은 외부 메시지 브로커가 필요하다.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    /**
     *  WebSocketConfig와 달리 Handler가 따로 필요없다. Handler는 Controller로 구현 가능하다.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-connect")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
