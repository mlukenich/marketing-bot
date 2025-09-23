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

@Service
public class ResearchService {

    private final ResearchResultRepository researchResultRepository;

    public ResearchService(ResearchResultRepository researchResultRepository) {
        this.researchResultRepository = researchResultRepository;
    }

    public List<ScrapedProductOffer> researchTrendingProducts(String trendingPageUrl) {
        List<ScrapedProductOffer> offers = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(trendingPageUrl).get();

            // Generic attempt to find product listings
            // This part would need to be highly customized for a real e-commerce site
            Elements productElements = doc.select("div.product-item, li.product, article.product");

            if (productElements.isEmpty()) {
                // Fallback: try to find common elements if specific classes aren't found
                productElements = doc.select("h2:has(a), h3:has(a)");
            }

            for (Element element : productElements) {
                ScrapedProductOffer offer = new ScrapedProductOffer();

                // Try to find product name
                Element nameElement = element.selectFirst("h2 a, h3 a, .product-title a");
                if (nameElement != null) {
                    offer.setName(nameElement.text());
                    offer.setUrl(nameElement.attr("abs:href"));
                } else {
                    // If no specific name element, try to get from the main element's text or first link
                    Element linkElement = element.selectFirst("a");
                    if (linkElement != null && !linkElement.text().isEmpty()) {
                        offer.setName(linkElement.text());
                        offer.setUrl(linkElement.attr("abs:href"));
                    } else {
                        offer.setName(element.text().substring(0, Math.min(element.text().length(), 50))); // Take first 50 chars
                        offer.setUrl(trendingPageUrl); // Fallback to the page URL
                    }
                }

                // Try to find description (e.g., from a paragraph or meta description if available)
                Element descElement = element.selectFirst("p.product-description, .product-excerpt");
                if (descElement != null) {
                    offer.setDescription(descElement.text());
                } else {
                    // Fallback to meta description if available, or empty
                    Element metaDescription = doc.select("meta[name=description]").first();
                    if (metaDescription != null) {
                        offer.setDescription(metaDescription.attr("content"));
                    } else {
                        offer.setDescription("");
                    }
                }

                // Try to find image URL
                Element imgElement = element.selectFirst("img.product-image, .product-thumbnail img");
                if (imgElement != null) {
                    offer.setImageUrl(imgElement.attr("abs:src"));
                }

                if (offer.getName() != null && !offer.getName().isEmpty() && offer.getUrl() != null && !offer.getUrl().isEmpty()) {
                    offers.add(offer);
                    // Save to database immediately after discovery
                    ResearchResult result = new ResearchResult();
                    result.setProductName(offer.getName());
                    result.setProductUrl(offer.getUrl());
                    result.setProductDescription(offer.getDescription());
                    result.setProductImageUrl(offer.getImageUrl());
                    researchResultRepository.save(result);
                }
            }

        } catch (IOException e) {
            throw new ScrapingException("Failed to research trending products from URL: " + trendingPageUrl, e);
        }
        return offers;
    }
}
