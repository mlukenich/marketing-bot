package com.marketer.affiliate_agent.dto;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LinkResponse {
    private Long id;
    private String title;
    private String longUrl;
    private String shortUrl;
    private String generatedContent;
    private LocalDateTime createdAt;
    private LocalDateTime scheduledAt;
    private boolean posted;
    private long clickCount;

    public static LinkResponse from(AffiliateLink link) {
        LinkResponse response = new LinkResponse();
        response.setId(link.getId());
        response.setTitle(link.getTitle());
        response.setLongUrl(link.getLongUrl());
        response.setShortUrl(link.getShortUrl());
        response.setGeneratedContent(link.getGeneratedContent());
        response.setCreatedAt(link.getCreatedAt());
        response.setScheduledAt(link.getScheduledAt());
        response.setPosted(link.isPosted());
        response.setClickCount(link.getClickCount());
        return response;
    }
}
