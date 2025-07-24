package lanit_exp.proxy_node.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurationModel {

    private String nodeId;

    private String serverUrl;
    private Integer serverPort;

    private String driverUrl;
    private Integer driverPort;


}
