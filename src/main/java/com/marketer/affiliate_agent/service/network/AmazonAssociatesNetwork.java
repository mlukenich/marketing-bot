package com.marketer.affiliate_agent.service.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class AmazonAssociatesNetwork implements AffiliateNetwork {

    private static final Logger log = LoggerFactory.getLogger(AmazonAssociatesNetwork.class);
    private final String amazonTag;

    public AmazonAssociatesNetwork(@Value("${affiliate.amazon.tag}") String amazonTag) {
        this.amazonTag = amazonTag;
    }

    @Override
    public boolean isApplicable(String url) {
        if (url == null) {
            return false;
        }
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            // Check if the host contains "amazon." to cover all international domains (e.g., .com, .co.uk, .de)
            return host != null && host.contains("amazon.");
        } catch (URISyntaxException e) {
            return false;
        }
    }

    @Override
    public String transform(String url) {
        if (!isApplicable(url)) {
            return url;
        }
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
            log.error("Failed to parse and transform Amazon URL: {}. Returning original URL.", url, e);
            return url;
        }
    }
}
