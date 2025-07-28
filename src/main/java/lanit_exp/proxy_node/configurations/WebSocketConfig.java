package lanit_exp.proxy_node.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.List;

@Configuration
public class WebSocketConfig {

    @Bean
    public WebSocketStompClient webSocketStompClient() {

        WebSocketClient client = new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient())));
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        stompClient.setMessageConverter(new StringMessageConverter());

        return stompClient;
    }


}
