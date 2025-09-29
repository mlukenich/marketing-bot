package com.marketer.affiliate_agent.repository;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AffiliateLinkRepository extends JpaRepository<AffiliateLink, Long> {
    Optional<AffiliateLink> findTopByOrderByScheduledAtDesc();

    List<AffiliateLink> findAllByClickCountGreaterThanEqualAndLastPostedAtBefore(long clickCount, LocalDateTime lastPostedAt);
}
