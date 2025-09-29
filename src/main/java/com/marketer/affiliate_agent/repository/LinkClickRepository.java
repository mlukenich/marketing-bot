package com.marketer.affiliate_agent.repository;

import com.marketer.affiliate_agent.entity.LinkClick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LinkClickRepository extends JpaRepository<LinkClick, Long> {
    // This new method correctly finds all clicks for a parent AffiliateLink by traversing the relationship.
    List<LinkClick> findByGeneratedContent_AffiliateLink_Id(Long affiliateLinkId);
}
