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
    private LocalDateTime createdAt;

    public static LinkResponse from(AffiliateLink link) {
        LinkResponse response = new LinkResponse();
        response.setId(link.getId());
        response.setTitle(link.getTitle());
        response.setLongUrl(link.getLongUrl());
        response.setShortUrl(link.getShortUrl());
        response.setCreatedAt(link.getCreatedAt());
        return response;
    }
}
