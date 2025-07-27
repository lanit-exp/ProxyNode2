package lanit_exp.proxy_node.controllers;

import lanit_exp.proxy_node.services.APIService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@AllArgsConstructor
@Slf4j
public class WebSocketSessionHandler implements StompSessionHandler {

    private final APIService apiService;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

        String nodeSession = ((DefaultStompSession) session).getConnectHeaders()
                .getNativeHeader("node_session")
                .get(0);
        session.subscribe("/queue/to/" + nodeSession, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {

                try {
                    log.info("[INPUT MES ] {}", payload);

                    System.out.println("Получено сообщение: " + payload);

                    StompHeaders h = new StompHeaders();
                    h.setDestination("/node/from");

                    String requestId = headers.get("request_id").stream()
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("В сообщении отсутствует обязательный хедер 'request_id'"));

                    h.add("request_id", requestId);

                    String response = apiService.proxyMessage((String) payload);

                    session.send(h, response);

                    log.info("[OUTPUT MES] {}", response);

                } catch (IllegalArgumentException e) {
                    log.error(e.getMessage());
                }
            }
        });

    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.out.println("handleException");
        throw new RuntimeException(exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        System.out.println(exception.getMessage());
        System.out.println("handleTransportError");
        throw new RuntimeException(exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.out.println("handleFrame");
    }
}
