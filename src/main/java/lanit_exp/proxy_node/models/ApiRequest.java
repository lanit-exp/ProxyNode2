package lanit_exp.proxy_node.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lanit_exp.proxy_node.helpers.StringHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiRequest {

    @JsonProperty
    private String method;
    @JsonProperty
    private String uri;
    @JsonProperty
    private LinkedMultiValueMap<String, String> headers;
    @JsonProperty
    private String body;


    @Override
    public String toString() {
        return "[ %s ]: %s\n%s".formatted(method, uri, StringHelper.trimLargeString(body, 500));
    }

    public HttpHeaders getHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.addAll(headers);
        h.remove("content-length");
        return h;
    }
}
