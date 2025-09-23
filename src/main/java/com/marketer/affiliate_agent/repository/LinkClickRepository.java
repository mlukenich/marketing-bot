package com.marketer.affiliate_agent.repository;

import com.marketer.affiliate_agent.entity.LinkClick;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkClickRepository extends JpaRepository<LinkClick, Long> {
}
