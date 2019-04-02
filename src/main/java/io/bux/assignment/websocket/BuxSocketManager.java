package io.bux.assignment.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.Locale;

@Configuration
@Slf4j
public class BuxSocketManager {

    @Value("${bux.websocket.endpoint}")
    private String baseEndpoint;

    @Value("${bux.headers.token}")
    private String authToken;

    @Value("${bux.headers.language}")
    private String languageHeader;

    @Autowired
    private BuxSocketHandler handler;

    private final String CONNECT_ENDPOINT = "/subscriptions/me";

    @Bean
    public WebSocketConnectionManager wsConnectionManager() {

        String endpoint = baseEndpoint + CONNECT_ENDPOINT;
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client(), handler, endpoint);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setAcceptLanguage(Locale.LanguageRange.parse(languageHeader));
        manager.setHeaders(headers);
        manager.setAutoStartup(true);
        return manager;
    }

    @Bean
    public StandardWebSocketClient client() {

        return new StandardWebSocketClient();
    }

}