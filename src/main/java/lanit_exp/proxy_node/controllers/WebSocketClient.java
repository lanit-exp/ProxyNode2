package lanit_exp.proxy_node.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class WebSocketClient {

    private final WebSocketStompClient webSocketStompClient;
    private final WebSocketSessionHandler webSocketSessionHandler;
    private StompSession session;

    public void connect(String url) {

        try {
            session = webSocketStompClient.connectAsync(url, webSocketSessionHandler).get();
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
