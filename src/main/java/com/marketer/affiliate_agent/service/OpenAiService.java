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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenAiService {

    @Value("${api-keys.openai}")
    private String openaiApiKey;

    @Value("${openai.api.url}")
    private String openaiApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<String> generatePostContent(String productName, String productDescription, ContentType contentType) {
        String prompt = buildPrompt(productName, productDescription, contentType);
        OpenAiRequest request = new OpenAiRequest("gpt-3.5-turbo", prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openaiApiKey);

        HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);

        try {
            OpenAiResponse response = restTemplate.postForObject(openaiApiUrl, entity, OpenAiResponse.class);

            if (response != null && !response.getChoices().isEmpty() && response.getChoices().get(0).getMessage() != null) {
                String rawContent = response.getChoices().get(0).getMessage().getContent();
                // Parse the response into a list, assuming the AI returns a numbered list on new lines
                return Arrays.stream(rawContent.split("\n"))
                        .map(line -> line.replaceAll("^\\d+\\.\\s*", "").trim()) // Remove numbering (e.g., "1. ") and trim whitespace
                        .filter(line -> !line.isEmpty())
                        .collect(Collectors.toList());
            } else {
                throw new ApiException("Invalid response from OpenAI API. Response was empty or malformed.");
            }
        } catch (RestClientException e) {
            throw new ApiException("Failed to call OpenAI API.", e);
        }
    }

    private String buildPrompt(String productName, String productDescription, ContentType contentType) {
        String basePrompt = String.format(
                "Generate 3 distinct and engaging ad copy variations for a product called '%s'. " +
                "Description: '%s'. " +
                "Each variation should be on a new line, starting with a number (e.g., '1. ').",
                productName, productDescription
        );

        switch (contentType) {
            case TWEET:
                return basePrompt + " The ad copy should be a short, engaging tweet under 280 characters, with a clear call to action and 2-3 relevant hashtags.";
            case BLOG_POST:
                return basePrompt + " The ad copy should be a short, engaging blog post of about 3-4 paragraphs, with an enthusiastic and informative tone, a catchy headline, and a clear call to action.";
            default:
                throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }
    }
}
