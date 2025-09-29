package com.marketer.affiliate_agent.dto;

import com.marketer.affiliate_agent.entity.GeneratedContent;
import lombok.Data;

@Data
public class GeneratedContentResponse {
    private Long id;
    private String content;
    private long clickCount;
    private boolean posted;

    public static GeneratedContentResponse from(GeneratedContent generatedContent) {
        GeneratedContentResponse response = new GeneratedContentResponse();
        response.setId(generatedContent.getId());
        response.setContent(generatedContent.getContent());
        response.setClickCount(generatedContent.getClickCount());
        response.setPosted(generatedContent.isPosted());
        return response;
    }
}
