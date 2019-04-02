package io.bux.assignment.client.request;

import lombok.Data;

@Data
public class InvestingAmount {

    private String currency;
    private int decimals;
    private String amount;
}
