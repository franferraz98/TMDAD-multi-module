package com.example.multimodule.application;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").withSockJS();
        registry.addEndpoint("/route").withSockJS();
        registry.addEndpoint("/createRoom").withSockJS();
        registry.addEndpoint("/addToRoom").withSockJS();
        registry.addEndpoint("/chatRoom").withSockJS();
        registry.addEndpoint("/notifications").withSockJS();
        registry.addEndpoint("/notify").withSockJS();
    }
}