package lanit_exp.proxy_node.controllers;

import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;

public class WebSocketSessionHandler implements StompSessionHandler {
    private StompSession stompSession;


    public void sendMessage(String message) {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.send("/node/message", message);
        } else {
            System.out.println("Session is not connected.");
        }
    }

    public static class Handler extends StompSessionHandlerAdapter {
        @Override
        public void handleFrame(StompHeaders headers, Object payload) {

            System.out.println("MESSAGA^^^ " + payload);

        }

    }






    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println(1);
        this.stompSession = session;
        System.out.println(2);

        session.send("/app/message", "Hello from client!");

        session.subscribe("/topic/messages", new Handler());
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
}
