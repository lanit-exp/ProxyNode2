package lanit_exp.proxy_node.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.List;

@Getter
@Setter
public class ConfigurationModel {

    private String nodeId;

    private String tags;

    private String serverUrl;
    private Integer serverPort;

    private String driverUrl;
    private Integer driverPort;


    public String getServerWSUrl() {
        return "ws://%s:%d/ws".formatted(
                serverUrl.replaceAll("^[a-z]+://", "")
                        .replaceAll("/$", ""),
                serverPort);
    }

    public StompHeaders getHeaders() {
        StompHeaders headers = new StompHeaders();

        headers.put("node_id", List.of(nodeId));
        headers.put("node_tags", List.of(tags));

        return headers;
    }

}
