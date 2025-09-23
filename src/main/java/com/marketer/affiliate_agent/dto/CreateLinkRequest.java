package com.marketer.affiliate_agent.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateLinkRequest {
    private String longUrl;
    private ContentType contentType = ContentType.TWEET; // Default to TWEET
    private LocalDateTime scheduledAt;
}
