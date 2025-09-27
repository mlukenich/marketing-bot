package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.GeneratedContent;
import com.marketer.affiliate_agent.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PinterestService implements SocialMediaService {

    private static final Logger log = LoggerFactory.getLogger(PinterestService.class);

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
        if (link.getProductImageUrl() == null || link.getProductImageUrl().isEmpty()) {
            log.warn("Skipping Pinterest post for link ID: {} because no product image is available.", link.getId());
            return;
        }

        List<GeneratedContent> contentVariations = link.getGeneratedContent();
        if (contentVariations == null || contentVariations.isEmpty()) {
            log.warn("Skipping Pinterest post for link ID: {} because no content is available.", link.getId());
            return;
        }

        String note = contentVariations.get(0).getContent();

        String pinterestApiUrl = "https://api.pinterest.com/v5/pins";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        Map<String, Object> mediaSource = new HashMap<>();
        mediaSource.put("source_type", "image_url");
        mediaSource.put("url", link.getProductImageUrl());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("board_id", boardId);
        requestBody.put("link", trackableUrl);
        requestBody.put("note", note);
        requestBody.put("media_source", mediaSource);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForObject(pinterestApiUrl, entity, String.class);
            log.info("Successfully posted to Pinterest for link ID: {}", link.getId());
        } catch (RestClientException e) {
            throw new ApiException("Failed to post to Pinterest: " + e.getMessage(), e);
        }
    }
}
