package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.dto.ScrapedProductInfo;
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

            // Extract the page title
            productInfo.setTitle(doc.title());

            // Extract the meta description
            Element metaDescription = doc.select("meta[name=description]").first();
            if (metaDescription != null) {
                productInfo.setDescription(metaDescription.attr("content"));
            } else {
                productInfo.setDescription(""); // Set an empty description if not found
            }

        } catch (IOException e) {
            // In a real application, you would handle this exception more gracefully
            e.printStackTrace();
            // Return a DTO with null or empty fields to indicate failure
            productInfo.setTitle("Could not scrape title");
            productInfo.setDescription("Could not scrape description");
        }
        return productInfo;
    }
}
