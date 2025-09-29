package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.service.network.AffiliateNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AffiliateTransformationService {

    private static final Logger log = LoggerFactory.getLogger(AffiliateTransformationService.class);
    private final List<AffiliateNetwork> affiliateNetworks;

    public AffiliateTransformationService(List<AffiliateNetwork> affiliateNetworks) {
        this.affiliateNetworks = affiliateNetworks;
        log.info("Loaded {} affiliate network implementations.", affiliateNetworks.size());
    }

    public String transform(String originalUrl) {
        if (originalUrl == null || originalUrl.isEmpty()) {
            return originalUrl;
        }

        // Find the first applicable network and delegate the transformation
        for (AffiliateNetwork network : affiliateNetworks) {
            if (network.isApplicable(originalUrl)) {
                log.debug("Transforming URL with network: {}", network.getClass().getSimpleName());
                return network.transform(originalUrl);
            }
        }

        // If no network is applicable, return the original URL
        log.debug("No applicable affiliate network found for URL: {}", originalUrl);
        return originalUrl;
    }
}
