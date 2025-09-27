package com.marketer.affiliate_agent.service.source;

import com.marketer.affiliate_agent.dto.ScrapedProductOffer;
import com.marketer.affiliate_agent.entity.ResearchResult;
import com.marketer.affiliate_agent.exception.ScrapingException;
import com.marketer.affiliate_agent.repository.ResearchResultRepository;
import com.marketer.affiliate_agent.util.JsoupWrapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmazonBestsellerSource implements ProductSource {

    private static final Logger log = LoggerFactory.getLogger(AmazonBestsellerSource.class);
    private final ResearchResultRepository researchResultRepository;
    private final JsoupWrapper jsoupWrapper;
    private final String trendingPageUrl;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36";

    public AmazonBestsellerSource(ResearchResultRepository researchResultRepository,
                                  JsoupWrapper jsoupWrapper,
                                  @Value("${research.trending.url}") String trendingPageUrl) {
        this.researchResultRepository = researchResultRepository;
        this.jsoupWrapper = jsoupWrapper;
        this.trendingPageUrl = trendingPageUrl;
    }

    @Override
    public List<ScrapedProductOffer> findProducts() {
        log.info("Finding products from Amazon Best Sellers: {}", trendingPageUrl);
        List<ScrapedProductOffer> offers = new ArrayList<>();
        try {
            Document doc = jsoupWrapper.connect(trendingPageUrl).userAgent(USER_AGENT).get();
            Elements productElements = doc.select("div.a-cardui.fluid-quad-card.fluid-card");

            for (Element element : productElements) {
                Element linkElement = element.selectFirst("a.a-link-normal");
                if (linkElement != null) {
                    String productUrl = linkElement.attr("abs:href");
                    if (researchResultRepository.findByProductUrl(productUrl).isEmpty()) {
                        ScrapedProductOffer offer = new ScrapedProductOffer();
                        offer.setUrl(productUrl);

                        Element imgElement = linkElement.selectFirst("img");
                        if (imgElement != null) {
                            offer.setName(imgElement.attr("alt"));
                            offer.setImageUrl(imgElement.attr("src"));
                        }

                        if (offer.getName() != null && !offer.getName().isEmpty()) {
                            String detailedDescription = scrapeProductDetails(productUrl);
                            offer.setDescription(detailedDescription);
                            offers.add(offer);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ScrapingException("Failed to find products from URL: " + trendingPageUrl, e);
        }
        return offers;
    }

    private String scrapeProductDetails(String productUrl) {
        try {
            Document productDoc = jsoupWrapper.connect(productUrl).userAgent(USER_AGENT).get();
            Elements featureBullets = productDoc.select("#feature-bullets .a-list-item");
            if (!featureBullets.isEmpty()) {
                return featureBullets.stream().map(Element::text).collect(Collectors.joining(". "));
            }
        } catch (IOException e) {
            log.warn("Failed to scrape details for {}: {}", productUrl, e.getMessage());
        }
        return "";
    }
}
