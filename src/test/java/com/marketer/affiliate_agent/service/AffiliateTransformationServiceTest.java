package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.service.network.AffiliateNetwork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AffiliateTransformationServiceTest {

    @Mock
    private AffiliateNetwork network1;

    @Mock
    private AffiliateNetwork network2;

    private AffiliateTransformationService affiliateTransformationService;

    @BeforeEach
    void setUp() {
        // Manually create the service with a list of our mock networks
        affiliateTransformationService = new AffiliateTransformationService(List.of(network1, network2));
    }

    @Test
    void transform_shouldDelegateToFirstApplicableNetwork() {
        // Arrange
        String originalUrl = "https://www.some-store.com/product";
        String transformedUrl = "https://www.some-store.com/product?tag=network2-tag";

        // Mock network1 to be not applicable
        when(network1.isApplicable(originalUrl)).thenReturn(false);

        // Mock network2 to be applicable and return a transformed URL
        when(network2.isApplicable(originalUrl)).thenReturn(true);
        when(network2.transform(originalUrl)).thenReturn(transformedUrl);

        // Act
        String result = affiliateTransformationService.transform(originalUrl);

        // Assert
        assertEquals(transformedUrl, result);

        // Verify that network1 was checked
        verify(network1).isApplicable(originalUrl);
        // Verify that network2 was checked and its transform method was called
        verify(network2).isApplicable(originalUrl);
        verify(network2).transform(originalUrl);
    }

    @Test
    void transform_shouldReturnOriginalUrl_whenNoNetworkIsApplicable() {
        // Arrange
        String originalUrl = "https://www.unsupported-store.com/product";

        // Mock all networks to be not applicable
        when(network1.isApplicable(originalUrl)).thenReturn(false);
        when(network2.isApplicable(originalUrl)).thenReturn(false);

        // Act
        String result = affiliateTransformationService.transform(originalUrl);

        // Assert
        assertEquals(originalUrl, result);

        // Verify that transform was never called on any network
        verify(network1, never()).transform(anyString());
        verify(network2, never()).transform(anyString());
    }
}
