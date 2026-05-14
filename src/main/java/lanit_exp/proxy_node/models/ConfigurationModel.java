package lanit_exp.proxy_node.models;

import lanit_exp.proxy_node.helpers.HostHelper;
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

    private String version;
    private String description;


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

        headers.set("node_id", nodeId);
        headers.set("node_tags", tags);

        headers.set("driver_names", drivers.stream()
                .map(Driver::getDriverName)
                .collect(Collectors.joining(",")));

        headers.set("node_name", HostHelper.getHostname());
        headers.set("node_version", version);
        headers.set("node_description", description);

        return headers;
    }

    public void addTags(String tags) {
        if (tags == null || tags.isEmpty()) return;

        if (this.tags == null || this.tags.isEmpty())
            this.tags = tags;
        else this.tags = this.tags + "," + tags;
    }

}
