package io.bux.assignment.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bux.assignment.service.TradeService;
import io.bux.assignment.websocket.message.BuxMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
public class BuxSocketHandler extends TextWebSocketHandler {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private ObjectMapper mapper;

    private ConcurrentWebSocketSessionDecorator session;
    private final static int SEND_TIMEOUT_SECONDS = 30;
    private final static int SEND_BUFFER_BYTES = 4096;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        this.session = new ConcurrentWebSocketSessionDecorator(session, SEND_TIMEOUT_SECONDS, SEND_BUFFER_BYTES);
    }

    /**
     * Handle 3 different types of BUX messages:
     *   connect.connected - great, carry on (do nothing)
     *   connect.failed - close the session, abort everything
     *   trading.quote - forward to tradeService
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage rawMessage) throws Exception {

        String payload = rawMessage.getPayload();
        log.debug("Received raw payload: {}", payload);
        BuxMessage message = mapper.readValue(payload, BuxMessage.class);
        if (message.getT() == null) {
            log.warn("Couldn't serialize raw socket message into BUX type: {}", payload);
            return;
        }

        try {
            if (message.getT().equals("trading.quote")) {
                tradeService.processTradeQuote(message);
            } else if (message.getT().equals("connect.connected")) {
                log.info("Received connect.connected event, proceeding with connection");
            } else if (message.getT().equals("connect.failed")) {
                log.error("Received connect.failed event, aborting session: {}", payload);
                session.close();
            } else {
                log.debug("Ignoring unknown BUX message type: " + message.getT());
            }
        } catch (Exception e) {
            log.error("Couldn't handle socket message due to: {}", e.getMessage());
        }
    }

    public void sendJsonMessage(Object object) throws Exception {

        session.sendMessage(new TextMessage(mapper.writeValueAsString(object)));
    }
}
