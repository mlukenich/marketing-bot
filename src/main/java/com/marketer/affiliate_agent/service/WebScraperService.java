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
            // Add a user agent to mimic a real browser
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
                    .get();

            productInfo.setTitle(doc.title());

            // Extract meta description
            Element metaDescription = doc.select("meta[name=description]").first();
            if (metaDescription != null) {
                productInfo.setDescription(metaDescription.attr("content"));
            } else {
                productInfo.setDescription("");
            }

            // Extract the main image URL from the 'og:image' meta tag
            Element imageElement = doc.select("meta[property=og:image]").first();
            if (imageElement != null) {
                productInfo.setImageUrl(imageElement.attr("content"));
            } else {
                productInfo.setImageUrl(""); // Set empty if not found
            }

            return productInfo;

        } catch (IOException e) {
            throw new ScrapingException("Failed to scrape content from URL: " + url, e);
        }
    }
}
