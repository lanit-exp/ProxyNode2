package lanit_exp.proxy_node.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lanit_exp.proxy_node.models.ApiRequest;
import lanit_exp.proxy_node.models.ConfigurationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class APIService {

    private final ConfigurationService configurationService;
    private final RestTemplate restTemplate;

    public String proxyMessage(ApiRequest message) {

        ConfigurationModel configurationModel = configurationService.getConfiguration();

        String url = "http://%s:%s%s".formatted(configurationModel.getDriverUrl(), configurationModel.getDriverPort(), message.getUri());
        HttpEntity<String> request = new HttpEntity<>(message.getBody(), message.getHeaders());

        try {

            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.valueOf(message.getMethod()), request, String.class);

            return new ObjectMapper().writeValueAsString(responseEntity);

        } catch (Exception e) {
            log.error("Ошибка отправки запроса драйверу: {}", e.getMessage());
            throw new RuntimeException("Ошибка отправки запроса драйверу: " + e.getMessage());
        }


    }


}
