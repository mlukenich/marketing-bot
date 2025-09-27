package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.dto.ScrapedProductOffer;
import com.marketer.affiliate_agent.entity.ResearchResult;
import com.marketer.affiliate_agent.repository.ResearchResultRepository;
import com.marketer.affiliate_agent.service.source.ProductSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResearchServiceTest {

    @Mock
    private ResearchResultRepository researchResultRepository;

    @Mock
    private ProductSource source1;

    @Mock
    private ProductSource source2;

    private ResearchService researchService;

    @BeforeEach
    void setUp() {
        // Manually create the service with the list of mock sources
        researchService = new ResearchService(List.of(source1, source2), researchResultRepository);
    }

    @Test
    void performResearch_shouldQueryAllSourcesAndSaveNewProducts() {
        // Arrange
        ScrapedProductOffer offer1 = new ScrapedProductOffer();
        offer1.setName("Product 1");
        offer1.setUrl("https://example.com/1");

        ScrapedProductOffer offer2 = new ScrapedProductOffer();
        offer2.setName("Product 2");
        offer2.setUrl("https://example.com/2");

        when(source1.findProducts()).thenReturn(List.of(offer1));
        when(source2.findProducts()).thenReturn(List.of(offer2));

        // Mock repository to indicate both products are new
        when(researchResultRepository.findByProductUrl(anyString())).thenReturn(Optional.empty());

        // Act
        researchService.performResearch();

        // Assert
        // Verify that findProducts was called on both sources
        verify(source1).findProducts();
        verify(source2).findProducts();

        // Verify that save was called twice with the correct data
        ArgumentCaptor<ResearchResult> resultCaptor = ArgumentCaptor.forClass(ResearchResult.class);
        verify(researchResultRepository, times(2)).save(resultCaptor.capture());

        List<ResearchResult> savedResults = resultCaptor.getAllValues();
        assertEquals("Product 1", savedResults.get(0).getProductName());
        assertEquals("Product 2", savedResults.get(1).getProductName());
    }

    @Test
    void performResearch_shouldHandleSourceFailureGracefully() {
        // Arrange
        ScrapedProductOffer offer2 = new ScrapedProductOffer();
        offer2.setName("Product 2");
        offer2.setUrl("https://example.com/2");

        // Simulate source1 failing
        when(source1.findProducts()).thenThrow(new RuntimeException("API is down"));
        // Simulate source2 succeeding
        when(source2.findProducts()).thenReturn(List.of(offer2));
        when(researchResultRepository.findByProductUrl(anyString())).thenReturn(Optional.empty());

        // Act
        researchService.performResearch();

        // Assert
        // Verify that both sources were still called
        verify(source1).findProducts();
        verify(source2).findProducts();

        // Verify that only the product from the successful source was saved
        verify(researchResultRepository, times(1)).save(any(ResearchResult.class));
    }
}
