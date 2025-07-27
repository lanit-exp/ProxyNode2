package lanit_exp.proxy_node.services;

import org.springframework.stereotype.Service;

@Service
public class APIService {


    public String proxyMessage(String message){

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return "Обработанное сообщение: " + message;

    }



}
