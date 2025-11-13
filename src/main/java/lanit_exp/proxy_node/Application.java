package lanit_exp.proxy_node;

import lanit_exp.proxy_node.services.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Application {

    public static void main(String[] args) throws InterruptedException {

        try {
            SpringApplication.run(Application.class, args)
                    .getBean(MainService.class)
                    .startNodeService();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        Thread.sleep(500);

        System.out.println("""
        
        
                ████████████████████████████████████████████████████
                █                                                  █
                █         Приложение завершило работу              █
                █                                                  █
                ████████████████████████████████████████████████████
        
                                       Нажмите Enter, чтобы выйти...            
        """);

        new java.util.Scanner(System.in).nextLine();
    }

}
