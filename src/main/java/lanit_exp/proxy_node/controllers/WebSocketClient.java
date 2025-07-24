package lanit_exp.proxy_node.controllers;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

@Component
public class WebSocketClient {

    private final WebSocketStompClient webSocketStompClient;
    private StompSession session;

    public WebSocketClient(WebSocketStompClient webSocketStompClient) {
        this.webSocketStompClient = webSocketStompClient;
    }


    public void connect(String url) {

        try {
            StompSession stompSession = webSocketStompClient.connectAsync(url, new StompSessionHandler() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

                    session.send("/node/message", "Hello from client!");

                    session.subscribe("/topic/mes", new WebSocketSessionHandler.Handler());
                    System.out.println("afterConnected");
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    System.out.println("handleException");
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    System.out.println(exception.getMessage());
                    System.out.println("handleTransportError");
                }

                @Override
                public Type getPayloadType(StompHeaders headers) {
                    System.out.println("getPayloadType");

                    return null;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    System.out.println("handleFrame");
                }
            }).get();

            this.session = stompSession;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
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
