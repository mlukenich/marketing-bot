package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.entity.AffiliateLink;
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
public class PinterestService implements SocialMediaService {

    private final String accessToken;
    private final String boardId;
    private final RestTemplate restTemplate;

    public PinterestService(@Value("${pinterest.accessToken}") String accessToken,
                            @Value("${pinterest.boardId}") String boardId) {
        this.accessToken = accessToken;
        this.boardId = boardId;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void post(AffiliateLink link, String trackableUrl) {
        // Pinterest requires an image to create a Pin.
        if (link.getProductImageUrl() == null || link.getProductImageUrl().isEmpty()) {
            System.err.println("Skipping Pinterest post for link ID: " + link.getId() + " because no product image is available.");
            return;
        }

        String pinterestApiUrl = "https://api.pinterest.com/v5/pins";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        // Construct the request body for the Pinterest API
        Map<String, Object> mediaSource = new HashMap<>();
        mediaSource.put("source_type", "image_url");
        mediaSource.put("url", link.getProductImageUrl());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("board_id", boardId);
        requestBody.put("link", trackableUrl);
        requestBody.put("note", link.getGeneratedContent());
        requestBody.put("media_source", mediaSource);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForObject(pinterestApiUrl, entity, String.class);
            System.out.println("Successfully posted to Pinterest for link ID: " + link.getId());
        } catch (RestClientException e) {
            throw new ApiException("Failed to post to Pinterest: " + e.getMessage(), e);
        }
    }
}
