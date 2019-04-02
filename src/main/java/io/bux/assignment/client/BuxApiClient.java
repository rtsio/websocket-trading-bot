package io.bux.assignment.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bux.assignment.client.exception.BuxApiException;
import io.bux.assignment.client.request.OpenPositionRequest;
import io.bux.assignment.client.response.ClosePositionResponse;
import io.bux.assignment.client.response.OpenPositionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Locale;

@Component
@Slf4j
public class BuxApiClient {

    @Value("${bux.api.endpoint}")
    private String baseEndpoint;

    @Value("${bux.headers.token}")
    private String authToken;

    @Value("${bux.headers.language}")
    private String languageHeader;

    private ObjectMapper objectMapper;
    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private final String OPEN_POSITION_URI = "/core/21/users/me/trades";
    private final String CLOSE_POSITION_URI = "/core/21/users/me/portfolio/positions";

    @Autowired
    public BuxApiClient (ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {

        headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setAcceptLanguage(Locale.LanguageRange.parse(languageHeader));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    }

    /**
     * Open a position given some product ID (using hardcoded values for investingAmount and etc.) using the BUX API
     * and return the resulting position ID
     */
    public String buyProduct(String productId) throws BuxApiException, JsonProcessingException {

        log.info("Sending request to open position for product ID = {}", productId);
        String url = baseEndpoint + OPEN_POSITION_URI;
        OpenPositionRequest request = new OpenPositionRequest(productId);
        String requestJson = objectMapper.writeValueAsString(request);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<OpenPositionResponse> response;

        try {
            response = restTemplate.postForEntity(url, entity, OpenPositionResponse.class);
            log.info("Request to open position using product ID {} succeeded with code {}", productId, response.getStatusCodeValue());
        } catch (RestClientResponseException ex) {
            log.error("Received error response: " + ex.getResponseBodyAsString());
            throw new BuxApiException("Received non-200 response while opening position via url: " + url + ", code: " + ex.getRawStatusCode(), ex);
        }

        return response.getBody().getPositionId();
    }

    /**
     * Close a position given some product ID using the BUX API
     */
    public void sellProduct(String positionId) throws BuxApiException {

        log.info("Sending request to close position for position ID = {}", positionId);
        String url = baseEndpoint + CLOSE_POSITION_URI + "/" + positionId;

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<ClosePositionResponse> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.DELETE, entity, ClosePositionResponse.class);
            log.info("Request to close position ID {} succeeded with code {}", positionId, response.getStatusCodeValue());
        } catch (RestClientResponseException ex) {
            log.error("Received error response: " + ex.getResponseBodyAsString());
            throw new BuxApiException("Received non-200 response while closing position via url: " + url + ", code: " + ex.getRawStatusCode(), ex);
        }
    }
}
