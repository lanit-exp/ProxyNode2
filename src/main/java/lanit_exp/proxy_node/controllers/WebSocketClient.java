package lanit_exp.proxy_node.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketClient {

    private final WebSocketStompClient webSocketStompClient;
    private final WebSocketSessionHandler webSocketSessionHandler;

    public StompSession connect(String serverWSUrl, StompHeaders stompHeaders) {

        try {
            StompSession session = webSocketStompClient.connectAsync(
                            serverWSUrl,
                            (WebSocketHttpHeaders) null,
                            stompHeaders,
                            webSocketSessionHandler)
                    .get();

            log.info("Соединение с сервером ProxyHub '{}' УСТАНОВЛЕНО", serverWSUrl);

            return session;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Не удалось установить соединение с сервером ProxyHub: '%s'"
                    .formatted(serverWSUrl), e);
        }

    }

}
