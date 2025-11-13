package lanit_exp.proxy_node.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lanit_exp.proxy_node.helpers.JsonHelper;
import lanit_exp.proxy_node.models.ApiRequest;
import lanit_exp.proxy_node.models.Driver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class APIService {

    private final RestTemplate restTemplate;
    private final DriverService driverService;

    public String proxyMessage(ApiRequest message, String driverName) throws JsonProcessingException {

        String uri = message.getUri();

        Matcher newSessionMatcher = Pattern.compile("^/session$").matcher(uri);
        Matcher sessionMatcher = Pattern.compile("^/session/([^/]+)").matcher(uri);

        ResponseEntity<String> responseEntity;

        try {
            if(newSessionMatcher.find()){
                responseEntity = proxyNewSessionMessage(message, driverName);
            } else if (sessionMatcher.find()){
                String sessionId = sessionMatcher.group(1);
                responseEntity = proxySessionMessage(message, sessionId);
            } else {
                responseEntity = new ResponseEntity<>(
                        "Неподдерживаемый запрос: '%s'. Отсутствует id сессии для идентификации запроса.".formatted(uri),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Ошибка отправки запроса драйверу: {}", e.getMessage());
            responseEntity = new ResponseEntity<>(
                    "Ошибка отправки запроса драйверу: '%s'.".formatted(e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        return new ObjectMapper().writeValueAsString(responseEntity);
    }


    private ResponseEntity<String> proxyNewSessionMessage(ApiRequest message, String driverName){

        Driver driver = driverService.getDriverByName(driverName);

        if (driver == null) throw new RuntimeException("У текущей ноды отсутствует драйвер с именем '%s'".formatted(driverName));

        HttpEntity<String> request = new HttpEntity<>(message.getBody(), message.getHeaders());

        ResponseEntity<String> response = restTemplate.exchange(driver.getFullUrl() + message.getUri(), HttpMethod.valueOf(message.getMethod()), request, String.class);

        String sessionId = JsonHelper.getValueByJsonPath(response.getBody(), "value", "sessionId");;

        driver.setDriverSession(sessionId);

        return response;
    }


    private ResponseEntity<String> proxySessionMessage(ApiRequest message, String sessionId){

        Driver driver = driverService.getDriverBySession(sessionId);

        if (driver == null) throw new RuntimeException("У текущей ноды отсутствует сессия с sessionId '%s'".formatted(sessionId));

        HttpEntity<String> request = new HttpEntity<>(message.getBody(), message.getHeaders());

        return restTemplate.exchange(driver.getFullUrl() + message.getUri(), HttpMethod.valueOf(message.getMethod()), request, String.class);
    }


}
