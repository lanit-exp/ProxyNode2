package lanit_exp.proxy_node.services;

import lanit_exp.proxy_node.models.Driver;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DriverService {

    @Setter
    private List<Driver> drivers;



}
