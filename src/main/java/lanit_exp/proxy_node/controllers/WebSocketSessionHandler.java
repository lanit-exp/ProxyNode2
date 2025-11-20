package lanit_exp.proxy_node.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lanit_exp.proxy_node.helpers.StringHelper;
import lanit_exp.proxy_node.models.ApiRequest;
import lanit_exp.proxy_node.services.APIService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

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
                    String driverName = getHeaderByName(headers, "driver_name");

                    String response = apiService.proxyMessage(apiRequest, driverName);

                    session.send(h, response);

                    log.info("[OUTPUT MES] {}", StringHelper.trimLargeString(response, 500));

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
            log.error("HandleFrame ERROR: {}", new ObjectMapper().writeValueAsString(headers));
        } catch (JsonProcessingException e) {
            log.error("HandleFrame ERROR");
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private static StompHeaders getResponseHeaders(StompHeaders requestHeaders) {
        StompHeaders result = new StompHeaders();
        result.setDestination("/node/from");

        String requestId = getHeaderByName(requestHeaders, "request_id");

        if (requestId == null)
            throw new IllegalArgumentException("В сообщении отсутствует обязательный хедер 'request_id'");

        result.add("request_id", requestId);

        return result;
    }

    private static String getHeaderByName(StompHeaders stompHeaders, String headerName) {
        List<String> strings = stompHeaders.get(headerName);
        if (strings == null || strings.isEmpty()) return null;
        return strings.get(0);
    }

}
