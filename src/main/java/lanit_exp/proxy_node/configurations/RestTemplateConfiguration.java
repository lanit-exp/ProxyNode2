package lanit_exp.proxy_node.configurations;

import lanit_exp.proxy_node.handlers.CustomResponseErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate getResttemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        return restTemplate;
    }

}
