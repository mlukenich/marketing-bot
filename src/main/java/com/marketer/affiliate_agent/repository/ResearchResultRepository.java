package com.marketer.affiliate_agent.repository;

import com.marketer.affiliate_agent.entity.ResearchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResearchResultRepository extends JpaRepository<ResearchResult, Long> {
    Optional<ResearchResult> findFirstByProcessedFalseOrderByDiscoveredAtAsc();

    Optional<ResearchResult> findByProductUrl(String productUrl);
}
