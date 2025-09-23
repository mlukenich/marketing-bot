package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.dto.ContentType;
import com.marketer.affiliate_agent.dto.openai.OpenAiRequest;
import com.marketer.affiliate_agent.dto.openai.OpenAiResponse;
import com.marketer.affiliate_agent.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAiService {

    @Value("${api-keys.openai}")
    private String openaiApiKey;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generatePostContent(String productName, String productDescription, ContentType contentType) {
        String prompt = buildPrompt(productName, productDescription, contentType);
        OpenAiRequest request = new OpenAiRequest("gpt-3.5-turbo", prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openaiApiKey);

        HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);

        try {
            OpenAiResponse response = restTemplate.postForObject(openaiApiUrl, entity, OpenAiResponse.class);

            if (response != null && !response.getChoices().isEmpty() && response.getChoices().get(0).getMessage() != null) {
                return response.getChoices().get(0).getMessage().getContent();
            } else {
                throw new ApiException("Invalid response from OpenAI API. Response was empty or malformed.");
            }
        } catch (RestClientException e) {
            throw new ApiException("Failed to call OpenAI API.", e);
        }
    }

    private String buildPrompt(String productName, String productDescription, ContentType contentType) {
        switch (contentType) {
            case TWEET:
                return String.format(
                        "Create a short, engaging tweet promoting a product called '%s'. " +
                                "Description: '%s'. " +
                                "Include a clear call to action and 2-3 relevant hashtags. " +
                                "Keep it under 280 characters.",
                        productName, productDescription
                );
            case BLOG_POST:
                return String.format(
                        "Write a short, engaging blog post of about 3-4 paragraphs promoting a product called '%s'. " +
                                "Description: '%s'. " +
                                "The tone should be enthusiastic and informative. " +
                                "Start with a catchy headline. " +
                                "End with a clear call to action encouraging readers to check out the product.",
                        productName, productDescription
                );
            default:
                throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }
    }
}
