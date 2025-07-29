package lanit_exp.proxy_node.controllers;

import lanit_exp.proxy_node.models.ConfigurationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketClient {

    private final WebSocketStompClient webSocketStompClient;
    private final WebSocketSessionHandler webSocketSessionHandler;

    public StompSession connect(ConfigurationModel configuration) {

        StompHeaders headers = configuration.getHeaders();

        headers.add("node_session", UUID.randomUUID().toString().replaceAll("-", ""));

        try {
            StompSession session = webSocketStompClient.connectAsync(configuration.getServerWSUrl(),
                            (WebSocketHttpHeaders) null,
                            headers,
                            webSocketSessionHandler)
                    .get();

            log.info("Соединение с сервером ProxyHub установленно");

            return session;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Не удалось установить соединение с сервером ProxyHub", e);
        }

    }

}
