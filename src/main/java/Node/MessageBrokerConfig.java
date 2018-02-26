package Node;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class MessageBrokerConfig extends AbstractWebSocketMessageBrokerConfigurer {
    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
//        return new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy());
        return new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy()){
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                              Map<String, Object> attributes) {
                return new Principal(){
                    private String username;
                    {
                        this.username = request.getURI().getUserInfo();
                    }
                    @Override
                    public boolean equals(Object another){
                        Principal other = (Principal)another;
                        return other.getName() == username;
                    }
                    @Override
                    public String getName(){
                        return username;
                    }

                    @Override
                    public int hashCode() {
                        return super.hashCode();
                    }
                    @Override
                    public String toString(){
                        return username;
                    }
                };
            }
        };
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //prefix for messages to be forwarded to broker
        config.enableSimpleBroker("/topic");
        //prefix for messages to be forwarded to controllers
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(handshakeHandler())
                .setAllowedOrigins("*")
                .withSockJS();
    }

//    @Override
//    public boolean configureMessageConverters(List<MessageConverter> messageConverters){
//        messageConverters.add(new MappingJackson2MessageConverter());
//        return false;
//    }
}

