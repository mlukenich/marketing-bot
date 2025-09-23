package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class BitlyService {

    @Value("${bitly.api.token}")
    private String bitlyToken;

    private final RestTemplate restTemplate = new RestTemplate(); // For simplicity. Ideally, this should be a bean.

    public String shortenUrl(String longUrl) {
        String bitlyUrl = "https://api-ssl.bitly.com/v4/shorten";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + bitlyToken);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("long_url", longUrl);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, String> response = restTemplate.postForObject(bitlyUrl, request, Map.class);
            if (response != null && response.containsKey("link")) {
                return response.get("link");
            } else {
                throw new ApiException("Invalid response from Bitly API. Response did not contain a link.");
            }
        } catch (RestClientException e) {
            throw new ApiException("Failed to call Bitly API.", e);
        }
    }
}
