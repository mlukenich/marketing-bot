package com.marketer.affiliate_agent.repository;

import com.marketer.affiliate_agent.entity.LinkClick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LinkClickRepository extends JpaRepository<LinkClick, Long> {
    void deleteByAffiliateLinkId(Long affiliateLinkId);

    List<LinkClick> findByAffiliateLinkId(Long affiliateLinkId);
}
