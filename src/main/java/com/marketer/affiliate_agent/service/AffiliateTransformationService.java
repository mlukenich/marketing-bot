package com.marketer.affiliate_agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class AffiliateTransformationService {

    private static final Logger log = LoggerFactory.getLogger(AffiliateTransformationService.class);
    private final String amazonTag;

    public AffiliateTransformationService(@Value("${affiliate.amazon.tag}") String amazonTag) {
        this.amazonTag = amazonTag;
    }

    public String transform(String originalUrl) {
        if (originalUrl == null || originalUrl.isEmpty()) {
            return originalUrl;
        }

        if (originalUrl.contains("amazon.com")) {
            return addAmazonTag(originalUrl);
        }

        return originalUrl;
    }

    private String addAmazonTag(String url) {
        try {
            URI oldUri = new URI(url);
            String newQuery = oldUri.getQuery();
            String queryParam = "tag=" + amazonTag;

            if (newQuery == null || newQuery.isEmpty()) {
                newQuery = queryParam;
            } else {
                newQuery += "&" + queryParam;
            }

            URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(), oldUri.getPath(), newQuery, oldUri.getFragment());
            return newUri.toString();

        } catch (URISyntaxException e) {
            log.error("Failed to parse and transform URL: {}. Returning original URL.", url, e);
            return url;
        }
    }
}
