package lanit_exp.proxy_node;

import lanit_exp.proxy_node.services.MainService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args)
                .getBean(MainService.class).startNodeService();
    }

}
