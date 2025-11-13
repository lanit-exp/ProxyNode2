package lanit_exp.proxy_node.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ConfigurationModel {

    private String nodeId;
    private String tags;

    private String serverUrl;
    private Integer serverPort;
    private Boolean https;

    private List<Driver> drivers;


    public String getServerWSUrl() {
        String protocol = https ? "wss://" : "ws://";
        String port = serverPort > 0 ? ":" + serverPort : "";

        return protocol
                + serverUrl.replaceAll("^[a-z]+://", "").replaceAll("/$", "")
                + port
                + "/ws";

    }

    public StompHeaders getHeaders() {
        StompHeaders headers = new StompHeaders();

        headers.put("node_id", List.of(nodeId));
        headers.put("node_tags", List.of(tags));

        headers.put("driver_names", List.of(drivers.stream()
                .map(Driver::getDriverName)
                .collect(Collectors.joining(","))));

        return headers;
    }

}
