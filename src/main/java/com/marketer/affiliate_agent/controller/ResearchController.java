package com.marketer.affiliate_agent.controller;

import com.marketer.affiliate_agent.entity.ResearchResult;
import com.marketer.affiliate_agent.repository.ResearchResultRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/research")
public class ResearchController {

    private final ResearchResultRepository researchResultRepository;

    public ResearchController(ResearchResultRepository researchResultRepository) {
        this.researchResultRepository = researchResultRepository;
    }

    @GetMapping
    public List<ResearchResult> getAllResearchResults() {
        return researchResultRepository.findAll();
    }
}
