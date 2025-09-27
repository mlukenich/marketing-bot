package com.marketer.affiliate_agent.service.source;

import com.marketer.affiliate_agent.dto.ScrapedProductOffer;

import java.util.List;

public interface ProductSource {
    List<ScrapedProductOffer> findProducts();
}
