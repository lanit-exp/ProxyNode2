package lanit_exp.proxy_node.services;

import lanit_exp.proxy_node.controllers.WebSocketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainService {

    public final ConfigurationService configurationService;
    public final WebSocketClient webSocketClient;


    public void startNodeService(){
        configurationService.getConfiguration();

    }


}
