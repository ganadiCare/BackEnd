    package smCapstone.homecam.global.config;

    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.socket.config.annotation.EnableWebSocket;
    import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
    import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
    import smCapstone.homecam.global.websocket.SignalingHandler;

    @Configuration
    @EnableWebSocket
    public class WebSocketConfig implements WebSocketConfigurer {

        private final SignalingHandler signalingHandler;

        public WebSocketConfig(SignalingHandler signalingHandler) {
            this.signalingHandler = signalingHandler;
        }

        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            registry.addHandler(signalingHandler, "/ws/signal")
                    .setAllowedOrigins("*");
        }
    }
