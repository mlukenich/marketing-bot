package com.marketer.affiliate_agent.repository;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AffiliateLinkRepository extends JpaRepository<AffiliateLink, Long> {
    List<AffiliateLink> findByScheduledAtBeforeAndPostedFalse(LocalDateTime dateTime);
}
