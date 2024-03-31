package ru.practicum.ewm.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> ResponseEntity<T> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<T> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<T> makeAndSendRequest(HttpMethod method, String path,
                                                     @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ParameterizedTypeReference<T> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<T> serverResponse;
        try {
            if (parameters != null) {
                serverResponse = rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                serverResponse = rest.exchange(path, method, requestEntity, responseType);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body((T) e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(serverResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static <T> ResponseEntity<T> prepareGatewayResponse(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
