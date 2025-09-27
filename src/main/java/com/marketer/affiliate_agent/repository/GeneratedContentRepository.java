package com.marketer.affiliate_agent.repository;

import com.marketer.affiliate_agent.entity.GeneratedContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface GeneratedContentRepository extends JpaRepository<GeneratedContent, Long> {
    Optional<GeneratedContent> findFirstByPostedFalseAndAffiliateLinkScheduledAtBeforeOrderByAffiliateLinkScheduledAtAsc(LocalDateTime dateTime);
}
