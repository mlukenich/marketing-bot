package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.dto.ScrapedProductOffer;
import com.marketer.affiliate_agent.entity.ResearchResult;
import com.marketer.affiliate_agent.exception.ScrapingException;
import com.marketer.affiliate_agent.repository.ResearchResultRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResearchService {

    private final ResearchResultRepository researchResultRepository;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36";

    public ResearchService(ResearchResultRepository researchResultRepository) {
        this.researchResultRepository = researchResultRepository;
    }

    public List<ScrapedProductOffer> researchTrendingProducts(String trendingPageUrl) {
        List<ScrapedProductOffer> offers = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(trendingPageUrl).userAgent(USER_AGENT).get();

            Elements productElements = doc.select("div.a-cardui.fluid-quad-card.fluid-card");

            for (Element element : productElements) {
                ScrapedProductOffer offer = new ScrapedProductOffer();

                Element linkElement = element.selectFirst("a.a-link-normal");
                if (linkElement != null) {
                    String productUrl = linkElement.attr("abs:href");
                    offer.setUrl(productUrl);

                    Element imgElement = linkElement.selectFirst("img");
                    if (imgElement != null) {
                        offer.setName(imgElement.attr("alt"));
                        offer.setImageUrl(imgElement.attr("src"));
                    }

                    if (offer.getName() != null && !offer.getName().isEmpty() && researchResultRepository.findByProductUrl(productUrl).isEmpty()) {
                        // Perform deep-dive scrape for detailed description
                        String detailedDescription = scrapeProductDetails(productUrl);
                        offer.setDescription(detailedDescription);

                        offers.add(offer);
                        ResearchResult result = new ResearchResult();
                        result.setProductName(offer.getName());
                        result.setProductUrl(offer.getUrl());
                        result.setProductDescription(offer.getDescription());
                        result.setProductImageUrl(offer.getImageUrl());
                        researchResultRepository.save(result);
                    }
                }
            }

        } catch (IOException e) {
            throw new ScrapingException("Failed to research trending products from URL: " + trendingPageUrl, e);
        }
        return offers;
    }

    private String scrapeProductDetails(String productUrl) {
        try {
            Document productDoc = Jsoup.connect(productUrl).userAgent(USER_AGENT).get();
            // Specific selector for Amazon's "About this item" bullet points
            Elements featureBullets = productDoc.select("#feature-bullets .a-list-item");
            if (!featureBullets.isEmpty()) {
                return featureBullets.stream()
                        .map(Element::text)
                        .collect(Collectors.joining(". "));
            }
        } catch (IOException e) {
            System.err.println("Failed to scrape details for " + productUrl + ": " + e.getMessage());
        }
        return ""; // Return empty string if details can't be found
    }
}
