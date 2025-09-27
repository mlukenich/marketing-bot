package com.marketer.affiliate_agent.scheduler;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.GeneratedContent;
import com.marketer.affiliate_agent.repository.GeneratedContentRepository;
import com.marketer.affiliate_agent.service.SocialMediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostingSchedulerTest {

    @Mock
    private GeneratedContentRepository generatedContentRepository;

    @Mock
    private SocialMediaService socialMediaService1;

    @Mock
    private SocialMediaService socialMediaService2;

    private PostingScheduler postingScheduler;

    @BeforeEach
    void setUp() {
        // Manually construct the scheduler with the correct dependencies
        postingScheduler = new PostingScheduler(generatedContentRepository, List.of(socialMediaService1, socialMediaService2));
        // Manually set the baseUrl property for testing
        ReflectionTestUtils.setField(postingScheduler, "baseUrl", "http://localhost:8085");
    }

    @Test
    void postScheduledContent_shouldPostNextAvailableContent() {
        // Arrange
        AffiliateLink parentLink = new AffiliateLink();
        parentLink.setTitle("Test Link");

        GeneratedContent contentToPost = new GeneratedContent();
        contentToPost.setId(101L);
        contentToPost.setAffiliateLink(parentLink);
        contentToPost.setPosted(false);

        when(generatedContentRepository.findFirstByPostedFalseAndAffiliateLinkScheduledAtBeforeOrderByAffiliateLinkScheduledAtAsc(any(LocalDateTime.class)))
                .thenReturn(Optional.of(contentToPost));

        // Act
        postingScheduler.postScheduledContent();

        // Assert
        String expectedTrackableUrl = "http://localhost:8085/track/101";

        // Verify that post was called on all services with the correct data
        verify(socialMediaService1).post(parentLink, expectedTrackableUrl);
        verify(socialMediaService2).post(parentLink, expectedTrackableUrl);

        // Verify that the specific content was marked as posted and saved
        ArgumentCaptor<GeneratedContent> contentCaptor = ArgumentCaptor.forClass(GeneratedContent.class);
        verify(generatedContentRepository).save(contentCaptor.capture());
        assertTrue(contentCaptor.getValue().isPosted());
    }

    @Test
    void postScheduledContent_shouldDoNothing_whenNoContentIsDue() {
        // Arrange
        when(generatedContentRepository.findFirstByPostedFalseAndAffiliateLinkScheduledAtBeforeOrderByAffiliateLinkScheduledAtAsc(any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // Act
        postingScheduler.postScheduledContent();

        // Assert
        // Verify that no services were called and no saves were attempted
        verify(socialMediaService1, never()).post(any(), any());
        verify(socialMediaService2, never()).post(any(), any());
        verify(generatedContentRepository, never()).save(any());
    }
}
