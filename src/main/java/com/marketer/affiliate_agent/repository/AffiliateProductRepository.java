package com.marketer.affiliate_agent.repository;

import com.marketer.affiliate_agent.model.AffiliateProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AffiliateProductRepository extends JpaRepository<AffiliateProduct, Long> {

    Optional<AffiliateProduct> findTopByOrderByLastPromotedAsc();
}
