package io.bux.assignment.websocket.message;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UnsubscribeMessage {

    private List<String> unsubscribeFrom;

    public UnsubscribeMessage(List<String> productIds) {
        unsubscribeFrom = productIds.stream().map(productId -> "trading.product." + productId).collect(Collectors.toList());
    }
}
