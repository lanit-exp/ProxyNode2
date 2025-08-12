package lanit_exp.proxy_node;

import lanit_exp.proxy_node.services.MainService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws InterruptedException {

        try {
            SpringApplication.run(Application.class, args)
                    .getBean(MainService.class)
                    .startNodeService();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.sleep(500);
        System.out.println("\n\n\n     Нажмите Enter, чтобы выйти...");
        new java.util.Scanner(System.in).nextLine();
    }

}
