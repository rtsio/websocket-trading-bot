package io.bux.assignment.websocket.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuxMessage {

    private String t;
    private String securityId;
    private String currentPrice;

    @JsonProperty("body")
    private void unpackNested(Map<String, Object> body) {
        this.securityId = (String) body.get("securityId");
        this.currentPrice = (String) body.get("currentPrice");
    }
}