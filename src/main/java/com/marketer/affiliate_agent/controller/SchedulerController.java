package com.marketer.affiliate_agent.controller;

import com.marketer.affiliate_agent.scheduler.MarketingScheduler;
import com.marketer.affiliate_agent.scheduler.ResearchScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/schedulers")
public class SchedulerController {

    private final ResearchScheduler researchScheduler;
    private final MarketingScheduler marketingScheduler;

    public SchedulerController(ResearchScheduler researchScheduler, MarketingScheduler marketingScheduler) {
        this.researchScheduler = researchScheduler;
        this.marketingScheduler = marketingScheduler;
    }

    @PostMapping("/research/trigger")
    public ResponseEntity<Map<String, String>> triggerResearch() {
        researchScheduler.performResearch();
        return ResponseEntity.ok(Map.of("message", "Research cycle triggered successfully."));
    }

    @PostMapping("/marketing/trigger")
    public ResponseEntity<Map<String, String>> triggerMarketing() {
        marketingScheduler.processResearchFindings();
        return ResponseEntity.ok(Map.of("message", "Marketing cycle triggered successfully."));
    }
}
