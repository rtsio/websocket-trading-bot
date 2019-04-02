package io.bux.assignment.service;

import io.bux.assignment.client.BuxApiClient;
import io.bux.assignment.exception.SubscribeError;
import io.bux.assignment.persistence.repository.TradePositionRepository;
import io.bux.assignment.websocket.BuxSocketHandler;
import io.bux.assignment.websocket.message.BuxMessage;
import io.bux.assignment.websocket.message.SubscribeMessage;
import io.bux.assignment.websocket.message.UnsubscribeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.bux.assignment.persistence.entity.TradePosition;

import java.util.Arrays;

@Service
@Slf4j
public class TradeService {

    @Autowired
    private TradePositionRepository tradeRepository;

    @Autowired
    private BuxSocketHandler websocketHandler;

    @Autowired
    private BuxApiClient apiClient;

    /**
     * Create a trade position in state store and subscribe to websocket messages for its product ID
     */
    public TradePosition createPosition(TradePosition position) throws Exception {

        log.info("Creating new TradePosition for requested product {}", position.getProductId());
        try {
            subscribeToProduct(position.getProductId());
        } catch (Exception e) {
            throw new SubscribeError("While creating position, could not subscribe to product updates: " + e.getMessage(), e);
        }
        return tradeRepository.save(position);
    }

    /**
     *  1. Check if given product ID was requested for a trade; if not, ignore (don't try to unsubscribe)
     *  2. If given product has not yet been bought; if currentPrice is less than or equal to buyPrice, and above the lower limit, buy and record position ID
     *  3. If given product has been bought:
     *      - if currentPrice is equal or greater than upper limit: sell, unsubscribe from messages, delete from DB (profit)
     *      - if currentPrice is equal or lesser than lower limit: unsubscribe from messages, delete from DB (loss)
     *     Things like query time/HTTP request time/retries are not considered; atomicity is not considered. In a production environment,
     *     this function should be structured asynchronously so that incoming message processing does not block.
     */
    public void processTradeQuote(BuxMessage message) {

        String productId = message.getSecurityId();
        Float currentPrice = Float.parseFloat(message.getCurrentPrice());
        TradePosition position = tradeRepository.findByProductId(productId);
        if (position == null) {
            log.warn("Got message for securityId = {}, but no position with this product ID is stored", productId);
            return;
        }

        if (!position.isOpen()) {
            log.debug("Product {} does not have an open position, buy price: {}, lower limit: {}, upper limit: {}, current: {}",
                    position.getProductId(),
                    position.getBuyPrice(),
                    position.getLowerLimit(),
                    position.getUpperLimit(),
                    currentPrice);
            if (currentPrice <= position.getBuyPrice() && currentPrice > position.getLowerLimit()) {
                try {
                    buyProduct(position);
                } catch (Exception e) {
                    log.error("While processing trade quote, could not execute buy:", e);
                }
            }
        } else {
            log.debug("Product {} already has an open position, buy price: {}, lower limit: {}, upper limit: {}, current: {}",
                    position.getProductId(),
                    position.getBuyPrice(),
                    position.getLowerLimit(),
                    position.getUpperLimit(),
                    currentPrice);
            if (currentPrice >= position.getUpperLimit() || currentPrice <= position.getLowerLimit()) {
                try {
                    sellProduct(position);
                } catch (Exception e) {
                    log.error("While processing trade quote, could not execute sell:", e);
                }
            }
        }
    }

    private void buyProduct(TradePosition position) throws Exception {

        log.info("Executing buy for product {}", position.getProductId());
        String positionId = apiClient.buyProduct(position.getProductId());
        position.setPositionId(positionId);
        tradeRepository.save(position);
    }

    private void sellProduct(TradePosition position) throws Exception {

        log.info("Executing sell for product {}", position.getProductId());
        apiClient.sellProduct(position.getPositionId());
        unsubscribeFromProduct(position.getProductId());
        tradeRepository.delete(position);
    }

    private void subscribeToProduct(String productId) throws Exception {

        log.info("Subscribing to messages for product {}", productId);
        SubscribeMessage message = new SubscribeMessage(Arrays.asList(productId));
        websocketHandler.sendJsonMessage(message);
    }

    private void unsubscribeFromProduct(String productId) throws Exception {

        log.info("Unsubscribing from messages for product {}", productId);
        UnsubscribeMessage message = new UnsubscribeMessage(Arrays.asList(productId));
        websocketHandler.sendJsonMessage(message);
    }
}
