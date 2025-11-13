package lanit_exp.proxy_node.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Driver {

    private final String url;
    private final Integer port;
    private final String driverName;

    private String driverSession;

    public Driver(String url, Integer port, String driverName) {
        this.url = url;
        this.port = port;
        this.driverName = driverName;
    }

    public synchronized String getDriverSession() {
        return driverSession;
    }

    public synchronized void setDriverSession(String driverSession) {
        this.driverSession = driverSession;
    }

    public String getFullUrl(){
        return "http://%s:%s".formatted(url, port);
    }
}
