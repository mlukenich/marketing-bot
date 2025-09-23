package com.marketer.affiliate_agent.repository;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AffiliateLinkRepository extends JpaRepository<AffiliateLink, Long> {
}
