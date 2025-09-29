package com.marketer.affiliate_agent.scheduler;

import com.marketer.affiliate_agent.dto.ContentType;
import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.ResearchResult;
import com.marketer.affiliate_agent.repository.AffiliateLinkRepository;
import com.marketer.affiliate_agent.repository.ResearchResultRepository;
import com.marketer.affiliate_agent.service.AffiliateLinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketingSchedulerTest {

    @Mock
    private ResearchResultRepository researchResultRepository;

    @Mock
    private AffiliateLinkRepository affiliateLinkRepository;

    @Mock
    private AffiliateLinkService affiliateLinkService;

    private MarketingScheduler marketingScheduler;

    @BeforeEach
    void setUp() {
        // Manually construct the scheduler, providing dummy values for all constructor arguments
        marketingScheduler = new MarketingScheduler(researchResultRepository, affiliateLinkRepository, affiliateLinkService, 120L, 9, 17);
    }

    @Test
    void processResearchFindings_shouldTriggerLinkCreation_whenUnprocessedResultExists() {
        // Arrange
        ResearchResult researchResult = new ResearchResult();
        researchResult.setId(1L);
        researchResult.setProductName("New Gadget");
        researchResult.setProductUrl("https://example.com/gadget");
        researchResult.setProcessed(false);

        when(researchResultRepository.findFirstByProcessedFalseOrderByDiscoveredAtAsc()).thenReturn(Optional.of(researchResult));
        // Mock the call to find the last scheduled link for staggering logic
        when(affiliateLinkRepository.findTopByOrderByScheduledAtDesc()).thenReturn(Optional.empty());

        // Act
        marketingScheduler.processResearchFindings();

        // Assert
        verify(affiliateLinkService, times(1)).createLink(
                eq("https://example.com/gadget"),
                any(ContentType.class),
                any(LocalDateTime.class)
        );

        ArgumentCaptor<ResearchResult> resultCaptor = ArgumentCaptor.forClass(ResearchResult.class);
        verify(researchResultRepository, times(1)).save(resultCaptor.capture());
        assertTrue(resultCaptor.getValue().isProcessed());
    }

    @Test
    void processResearchFindings_shouldDoNothing_whenNoUnprocessedResultsExist() {
        // Arrange
        when(researchResultRepository.findFirstByProcessedFalseOrderByDiscoveredAtAsc()).thenReturn(Optional.empty());

        // Act
        marketingScheduler.processResearchFindings();

        // Assert
        verify(affiliateLinkService, never()).createLink(any(), any(), any());
        verify(researchResultRepository, never()).save(any());
    }
}
