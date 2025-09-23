package com.marketer.affiliate_agent.controller;

import com.marketer.affiliate_agent.service.BitlyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/links")
public class AffiliateController {

    private final BitlyService bitlyService;

    public AffiliateController(BitlyService bitlyService) {
        this.bitlyService = bitlyService;
    }

    @PostMapping("/shorten")
    public String shortenUrl(@RequestBody String longUrl) {
        return bitlyService.shortenUrl(longUrl);
    }
}
