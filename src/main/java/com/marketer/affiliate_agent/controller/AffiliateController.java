package com.marketer.affiliate_agent.controller;

import com.marketer.affiliate_agent.dto.CreateLinkRequest;
import com.marketer.affiliate_agent.dto.LinkClickResponse;
import com.marketer.affiliate_agent.dto.LinkResponse;
import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.service.AffiliateLinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/links")
public class AffiliateController {

    private final AffiliateLinkService affiliateLinkService;

    public AffiliateController(AffiliateLinkService affiliateLinkService) {
        this.affiliateLinkService = affiliateLinkService;
    }

    @PostMapping
    public LinkResponse createLink(@RequestBody CreateLinkRequest request) {
        AffiliateLink newLink = affiliateLinkService.createLink(request.getLongUrl(), request.getContentType(), request.getScheduledAt());
        return LinkResponse.from(newLink);
    }

    @GetMapping
    public List<LinkResponse> getAllLinks() {
        return affiliateLinkService.getAllLinks().stream()
                .map(LinkResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public LinkResponse getLinkById(@PathVariable Long id) {
        return LinkResponse.from(affiliateLinkService.getLinkById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable Long id) {
        affiliateLinkService.deleteLink(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/clicks")
    public List<LinkClickResponse> getLinkClicks(@PathVariable Long id) {
        return affiliateLinkService.getLinkClicks(id).stream()
                .map(LinkClickResponse::from)
                .collect(Collectors.toList());
    }
}
