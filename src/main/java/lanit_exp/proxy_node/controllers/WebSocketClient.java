package lanit_exp.proxy_node.controllers;

import lanit_exp.proxy_node.models.ConfigurationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class WebSocketClient {

    private final WebSocketStompClient webSocketStompClient;
    private final WebSocketSessionHandler webSocketSessionHandler;
    private StompSession session;

    public void connect(ConfigurationModel configuration) {

        StompHeaders headers = configuration.getHeaders();
        headers.add("node_session", UUID.randomUUID().toString().replaceAll("-", ""));

        try {
            session = webSocketStompClient.connectAsync(configuration.getServerWSUrl(),
                            (WebSocketHttpHeaders) null,
                            headers,
                            webSocketSessionHandler)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Не удалось установить соединение с сервером ProxyHub", e);
        }

    }


    public void sendMessage(String message) {
        if (session != null && session.isConnected()) {
            session.send("/node/message", message);
        } else {
            System.out.println("Session is not connected.");
        }
    }


}
