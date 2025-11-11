package lanit_exp.proxy_node.configurations;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Configuration
public class WebSocketConfig {

    @Value("${ws.message.size_limit}")
    private Integer messageSizeLimit;

    @Bean
    public WebSocketStompClient webSocketStompClient() {

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxTextMessageBufferSize(messageSizeLimit);
        container.setDefaultMaxBinaryMessageBufferSize(messageSizeLimit);

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient(container));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        return stompClient;
    }


}
