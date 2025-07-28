package lanit_exp.proxy_node.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lanit_exp.proxy_node.models.ApiRequest;
import lanit_exp.proxy_node.models.ConfigurationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class APIService {

    private final ConfigurationService configurationService;

    public String proxyMessage(ApiRequest message) {

        ConfigurationModel configurationModel = configurationService.getConfiguration();

        WebClient webClient = WebClient.create("http://%s:%s".formatted(configurationModel.getDriverUrl(), configurationModel.getDriverPort()));

        try {
            ResponseEntity<String> entity = webClient.method(HttpMethod.valueOf(message.getMethod()))
                    .uri(message.getUri())
                    .bodyValue(message.getBody())
                    .headers(httpHeaders -> httpHeaders.addAll(message.getHeaders()))
                    .exchangeToMono(clientResponse -> {
                        HttpStatus statusCode = (HttpStatus) clientResponse.statusCode();
                        HttpHeaders headers = clientResponse.headers().asHttpHeaders();

                        return clientResponse.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(statusCode)
                                        .headers(h -> h.addAll(headers))
                                        .body(body));
                    }).block();


            return new ObjectMapper().writeValueAsString(entity);

        } catch (Exception e) {
            log.error("Ошибка отправки запроса драйверу: {}", e.getMessage());
            throw new RuntimeException("Ошибка отправки запроса драйверу: " + e.getMessage());
        }

    }


}
