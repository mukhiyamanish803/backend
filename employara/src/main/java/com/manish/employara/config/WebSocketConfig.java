package com.manish.employara.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptorAdapter authChannelInterceptorAdapter;

    // Configure message broker (simple broker, prefixes)
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // Use "/queue" and "/topic" for point-to-point and broadcast messaging
        config.enableSimpleBroker("/queue", "/topic"); // Good for dev/small scale, replace with external broker (e.g.,
                                                       // RabbitMQ) for production
        config.setApplicationDestinationPrefixes("/app"); // Where client sends messages (e.g., /app/send)
        config.setUserDestinationPrefix("/user"); // Enables /user/{username}/queue for private messages
    }

    // Register STOMP endpoint for client connections
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // WebSocket endpoint with SockJS fallback for unsupported browsers
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173"); // In production, restrict to known origins
    }

    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptorAdapter);
    }
}
