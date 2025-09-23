package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.dto.ScrapedProductInfo;
import com.marketer.affiliate_agent.exception.ScrapingException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WebScraperService {

    public ScrapedProductInfo scrapeProductInfo(String url) {
        ScrapedProductInfo productInfo = new ScrapedProductInfo();
        try {
            Document doc = Jsoup.connect(url).get();

            productInfo.setTitle(doc.title());

            Element metaDescription = doc.select("meta[name=description]").first();
            if (metaDescription != null) {
                productInfo.setDescription(metaDescription.attr("content"));
            } else {
                // If no description is found, we can decide if this is an error or not.
                // For now, we'll let it proceed with an empty description.
                productInfo.setDescription("");
            }
            return productInfo;

        } catch (IOException e) {
            throw new ScrapingException("Failed to scrape content from URL: " + url, e);
        }
    }
}
