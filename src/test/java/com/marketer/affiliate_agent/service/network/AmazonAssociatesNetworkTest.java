package com.marketer.affiliate_agent.service.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmazonAssociatesNetworkTest {

    private AmazonAssociatesNetwork amazonAssociatesNetwork;

    @BeforeEach
    void setUp() {
        // Set up the service with a dummy affiliate tag for testing
        amazonAssociatesNetwork = new AmazonAssociatesNetwork("testtag-20");
    }

    @Test
    void isApplicable_shouldReturnTrue_forAmazonUrls() {
        assertTrue(amazonAssociatesNetwork.isApplicable("https://www.amazon.com/dp/B081V6W99V"));
        assertTrue(amazonAssociatesNetwork.isApplicable("https://www.amazon.co.uk/product"));
    }

    @Test
    void isApplicable_shouldReturnFalse_forNonAmazonUrls() {
        assertFalse(amazonAssociatesNetwork.isApplicable("https://www.example.com"));
        assertFalse(amazonAssociatesNetwork.isApplicable(null));
    }

    @Test
    void transform_shouldAddTagToCleanAmazonUrl() {
        String originalUrl = "https://www.amazon.com/dp/B081V6W99V";
        String expectedUrl = "https://www.amazon.com/dp/B081V6W99V?tag=testtag-20";
        String transformedUrl = amazonAssociatesNetwork.transform(originalUrl);
        assertEquals(expectedUrl, transformedUrl);
    }

    @Test
    void transform_shouldAppendTagToAmazonUrlWithExistingParams() {
        String originalUrl = "https://www.amazon.com/dp/B081V6W99V?other=param";
        String expectedUrl = "https://www.amazon.com/dp/B081V6W99V?other=param&tag=testtag-20";
        String transformedUrl = amazonAssociatesNetwork.transform(originalUrl);
        assertEquals(expectedUrl, transformedUrl);
    }

    @Test
    void transform_shouldReturnOriginalUrl_forNonAmazonUrl() {
        String originalUrl = "https://www.example.com/product/123";
        String transformedUrl = amazonAssociatesNetwork.transform(originalUrl);
        assertEquals(originalUrl, transformedUrl);
    }
}
