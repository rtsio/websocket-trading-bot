package io.bux.assignment.persistence.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * A TradePosition represents a trade the user wants to make; it can optionally correspond to an actual open/closed
 * position on a market.
 */
@Entity
@Data
public class TradePosition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String productId;

    @Column(nullable = true)
    private String positionId;

    @Column(nullable = false)
    private Float buyPrice;

    @Column(nullable = false)
    private Float lowerLimit;

    @Column(nullable = false)
    private Float upperLimit;

    public boolean isOpen() {
        return positionId != null;
    }
}
