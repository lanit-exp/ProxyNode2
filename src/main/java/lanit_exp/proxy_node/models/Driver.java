package lanit_exp.proxy_node.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Driver {

    private final String url;
    private final Integer port;
    private final String driverName;

    @Setter
    private String driverSession;

    public Driver(String url, Integer port, String driverName) {
        this.url = url;
        this.port = port;
        this.driverName = driverName;
    }

}
