package lanit_exp.proxy_node.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lanit_exp.proxy_node.models.ApiRequest;
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

        String nodeSession = connectedHeaders.get("node_session").get(0);
        session.subscribe("/queue/to/" + nodeSession, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ApiRequest.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {

                try {

                    ApiRequest apiRequest = (ApiRequest) payload;

                    log.info("[INPUT MES ] {}", apiRequest);

                    StompHeaders h = getResponseHeaders(headers);

                    String response = apiService.proxyMessage(apiRequest);

                    session.send(h, response);

                    log.info("[OUTPUT MES] {}", response.length() > 1000 ?
                            response.substring(0, 1000) + "...<length: %d>".formatted(response.length()) : response);

                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        });

    }


    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        StompHeaders h = getResponseHeaders(headers);

        session.send(h, "Ошибка обработки запроса от ProxyHub: " + exception.getMessage());

        log.error("Ошибка обработки запроса от ProxyHub", exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.error("Ошибка обработки запроса от ProxyHub: {}", exception.getMessage());
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Object.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        try {
            log.error("HandleFrame ERROR: {}",  new ObjectMapper().writeValueAsString(headers));
        } catch (JsonProcessingException e) {
            log.error("HandleFrame ERROR");
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private static StompHeaders getResponseHeaders(StompHeaders requestHeaders){
        StompHeaders result = new StompHeaders();
        result.setDestination("/node/from");

        String requestId = requestHeaders.get("request_id").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("В сообщении отсутствует обязательный хедер 'request_id'"));

        result.add("request_id", requestId);

        return result;
    }
}
