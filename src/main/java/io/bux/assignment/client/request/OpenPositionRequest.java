package io.bux.assignment.client.request;

import lombok.Data;

@Data
public class OpenPositionRequest {

    private String productId;
    private InvestingAmount investingAmount;
    private int leverage;
    private String direction;
    private Source source;

    /**
     * All values exception for productId are hardcoded!
     */
    public OpenPositionRequest(String productId) {

        // TODO: what do all these other properties mean?
        this.productId = productId;
        investingAmount = new InvestingAmount();
        investingAmount.setCurrency("BUX");
        investingAmount.setDecimals(2);
        investingAmount.setAmount("10.00");
        leverage = 2;
        direction = "BUY";
        source = new Source();
        source.setSourceType("OTHER");
    }
}
