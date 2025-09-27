package com.marketer.affiliate_agent.scheduler;

import com.marketer.affiliate_agent.service.ResearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ResearchScheduler {

    private static final Logger log = LoggerFactory.getLogger(ResearchScheduler.class);

    private final ResearchService researchService;

    public ResearchScheduler(ResearchService researchService) {
        this.researchService = researchService;
    }

    @Scheduled(cron = "${scheduler.research.cron}")
    public void performResearchCycle() {
        log.info("Triggering research cycle as per cron schedule...");
        try {
            researchService.performResearch();
        } catch (Exception e) {
            log.error("An unexpected error occurred during the research cycle", e);
        }
    }
}
