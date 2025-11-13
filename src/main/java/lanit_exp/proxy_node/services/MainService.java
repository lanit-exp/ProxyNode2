package lanit_exp.proxy_node.services;

import lanit_exp.proxy_node.controllers.WebSocketClient;
import lanit_exp.proxy_node.models.ConfigurationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainService {

    private final ConfigurationService configurationService;
    private final WebSocketClient webSocketClient;
    private final DriverService driverService;

    @Value("${node.reconnect.time}")
    private Integer reconnectTime;


    public void startNodeService() {
        ConfigurationModel configuration = configurationService.getConfiguration();

        if (configuration == null) return;

        driverService.setDrivers(configuration.getDrivers());

        String serverWSUrl = configuration.getServerWSUrl();
        StompHeaders stompHeaders = configuration.getHeaders();

        StompSession session = null;

        while (true) {

            try {
                if (session == null || !session.isConnected()) {
                    log.info("Попытка подключения к {}", serverWSUrl);
                    session = webSocketClient.connect(serverWSUrl, stompHeaders);
                }
            } catch (Exception e) {
                log.error("Не удалось подключиться к ProxyNode: '{}'.", e.getMessage());
                log.info("Повторная попытка подключения через {} сек...", reconnectTime);
            }

            try {
                Thread.sleep(reconnectTime * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }


    }


}
