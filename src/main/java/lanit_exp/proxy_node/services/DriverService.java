package lanit_exp.proxy_node.services;

import lanit_exp.proxy_node.models.Driver;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class DriverService {

    @Setter
    private List<Driver> drivers;

    public Driver getDriverByName(String driverName) {
        if (driverName == null || driverName.isEmpty()) return drivers.get(0);

        return drivers.stream().filter(driver -> Objects.equals(driver.getDriverName(), driverName))
                .findFirst()
                .orElse(null);
    }

    public Driver getDriverBySession(String sessionId) {
        return drivers.stream().filter(driver -> Objects.equals(driver.getDriverSession(), sessionId))
                .findFirst()
                .orElse(null);
    }
}
