package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.dto.ScrapedProductOffer;
import com.marketer.affiliate_agent.entity.ResearchResult;
import com.marketer.affiliate_agent.repository.ResearchResultRepository;
import com.marketer.affiliate_agent.service.source.ProductSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResearchService {

    private static final Logger log = LoggerFactory.getLogger(ResearchService.class);
    private final List<ProductSource> productSources;
    private final ResearchResultRepository researchResultRepository;

    public ResearchService(List<ProductSource> productSources, ResearchResultRepository researchResultRepository) {
        this.productSources = productSources;
        this.researchResultRepository = researchResultRepository;
    }

    public void performResearch() {
        log.info("Starting research cycle across {} sources...", productSources.size());

        for (ProductSource source : productSources) {
            log.info("Querying product source: {}", source.getClass().getSimpleName());
            try {
                List<ScrapedProductOffer> offers = source.findProducts();
                log.info("Found {} potential offers from {}.", offers.size(), source.getClass().getSimpleName());

                for (ScrapedProductOffer offer : offers) {
                    // Ensure the product hasn't been discovered already
                    if (researchResultRepository.findByProductUrl(offer.getUrl()).isEmpty()) {
                        ResearchResult result = new ResearchResult();
                        result.setProductName(offer.getName());
                        result.setProductUrl(offer.getUrl());
                        result.setProductDescription(offer.getDescription());
                        result.setProductImageUrl(offer.getImageUrl());
                        researchResultRepository.save(result);
                        log.info("Saved new product offer: {}", offer.getName());
                    } else {
                        log.debug("Skipping already discovered product: {}", offer.getName());
                    }
                }
            } catch (Exception e) {
                log.error("Failed to find products from source {}: {}", source.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
        log.info("Research cycle finished.");
    }
}
