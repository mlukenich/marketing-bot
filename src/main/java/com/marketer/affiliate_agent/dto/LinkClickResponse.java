package com.marketer.affiliate_agent.dto;

import com.marketer.affiliate_agent.entity.LinkClick;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LinkClickResponse {
    private Long id;
    private LocalDateTime clickedAt;

    public static LinkClickResponse from(LinkClick linkClick) {
        LinkClickResponse response = new LinkClickResponse();
        response.setId(linkClick.getId());
        response.setClickedAt(linkClick.getClickedAt());
        return response;
    }
}
