package com.marketer.affiliate_agent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AffiliateTransformationServiceTest {

    private AffiliateTransformationService affiliateTransformationService;

    @BeforeEach
    void setUp() {
        // Set up the service with a dummy affiliate tag for testing
        affiliateTransformationService = new AffiliateTransformationService("testtag-20");
    }

    @Test
    void transform_shouldAddTagToCleanAmazonUrl() {
        String originalUrl = "https://www.amazon.com/dp/B081V6W99V";
        String expectedUrl = "https://www.amazon.com/dp/B081V6W99V?tag=testtag-20";
        String transformedUrl = affiliateTransformationService.transform(originalUrl);
        assertEquals(expectedUrl, transformedUrl);
    }

    @Test
    void transform_shouldAppendTagToAmazonUrlWithExistingParams() {
        String originalUrl = "https://www.amazon.com/dp/B081V6W99V?other=param";
        String expectedUrl = "https://www.amazon.com/dp/B081V6W99V?other=param&tag=testtag-20";
        String transformedUrl = affiliateTransformationService.transform(originalUrl);
        assertEquals(expectedUrl, transformedUrl);
    }

    @Test
    void transform_shouldNotChangeNonAmazonUrl() {
        String originalUrl = "https://www.example.com/product/123";
        String transformedUrl = affiliateTransformationService.transform(originalUrl);
        assertEquals(originalUrl, transformedUrl);
    }

    @Test
    void transform_shouldHandleUrlWithFragment() {
        String originalUrl = "https://www.amazon.com/dp/B081V6W99V#customerReviews";
        String expectedUrl = "https://www.amazon.com/dp/B081V6W99V?tag=testtag-20#customerReviews";
        String transformedUrl = affiliateTransformationService.transform(originalUrl);
        assertEquals(expectedUrl, transformedUrl);
    }

    @Test
    void transform_shouldReturnOriginalUrl_whenUrlIsInvalid() {
        String invalidUrl = "not a valid url";
        String transformedUrl = affiliateTransformationService.transform(invalidUrl);
        assertEquals(invalidUrl, transformedUrl);
    }

    @Test
    void transform_shouldHandleNullAndEmptyUrls() {
        assertEquals(null, affiliateTransformationService.transform(null));
        assertEquals("", affiliateTransformationService.transform(""));
    }
}
