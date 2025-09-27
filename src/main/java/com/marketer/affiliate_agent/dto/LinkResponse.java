package com.marketer.affiliate_agent.dto;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.GeneratedContent;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LinkResponse {
    private Long id;
    private String title;
    private String longUrl;
    private String shortUrl;
    private List<String> generatedContent;
    private String productImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime scheduledAt;
    private long clickCount;

    public static LinkResponse from(AffiliateLink link) {
        LinkResponse response = new LinkResponse();
        response.setId(link.getId());
        response.setTitle(link.getTitle());
        response.setLongUrl(link.getLongUrl());
        response.setShortUrl(link.getShortUrl());
        response.setProductImageUrl(link.getProductImageUrl());
        response.setCreatedAt(link.getCreatedAt());
        response.setScheduledAt(link.getScheduledAt());
        response.setClickCount(link.getClickCount());

        if (link.getGeneratedContent() != null) {
            response.setGeneratedContent(link.getGeneratedContent().stream()
                    .map(GeneratedContent::getContent)
                    .collect(Collectors.toList()));
        }

        return response;
    }
}
