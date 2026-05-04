package lanit_exp.proxy_node.models;


import lombok.Getter;

public class Driver {

    private final String url;


    private final Integer port;

    @Getter
    private final String driverName;

    @Getter
    private final String beforeScriptPath;


    private String driverSession;


    //------------------------------------------------------------------------------------------------------------------

    public Driver(String url, Integer port, String driverName, String beforeScriptPath) {
        this.url = url;
        this.port = port;
        this.driverName = driverName;
        this.beforeScriptPath = beforeScriptPath;
    }

    public synchronized String getDriverSession() {
        return driverSession;
    }

    public synchronized void setDriverSession(String driverSession) {
        this.driverSession = driverSession;
    }

    public String getFullUrl() {
        return "http://%s:%s".formatted(url, port);
    }
}
