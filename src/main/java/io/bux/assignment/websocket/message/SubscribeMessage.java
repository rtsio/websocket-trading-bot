package io.bux.assignment.websocket.message;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class SubscribeMessage {

    private List<String> subscribeTo;

    public SubscribeMessage(List<String> productIds) {
        subscribeTo = productIds.stream().map(productId -> "trading.product." + productId).collect(Collectors.toList());
    }
}
