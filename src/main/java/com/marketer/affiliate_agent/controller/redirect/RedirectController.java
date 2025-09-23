package com.marketer.affiliate_agent.controller.redirect;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.LinkClick;
import com.marketer.affiliate_agent.exception.ApiException;
import com.marketer.affiliate_agent.repository.AffiliateLinkRepository;
import com.marketer.affiliate_agent.repository.LinkClickRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

@Controller
public class RedirectController {

    private final AffiliateLinkRepository affiliateLinkRepository;
    private final LinkClickRepository linkClickRepository;

    public RedirectController(AffiliateLinkRepository affiliateLinkRepository, LinkClickRepository linkClickRepository) {
        this.affiliateLinkRepository = affiliateLinkRepository;
        this.linkClickRepository = linkClickRepository;
    }

    @GetMapping("/track/{linkId}")
    @Transactional
    public void redirectToLongUrl(@PathVariable Long linkId, HttpServletResponse response) throws IOException {
        AffiliateLink affiliateLink = affiliateLinkRepository.findById(linkId)
                .orElseThrow(() -> new ApiException("Affiliate link not found for ID: " + linkId));

        // Record the click
        LinkClick click = new LinkClick();
        click.setAffiliateLink(affiliateLink);
        linkClickRepository.save(click);

        // Increment click count on the affiliate link
        affiliateLink.setClickCount(affiliateLink.getClickCount() + 1);
        affiliateLinkRepository.save(affiliateLink);

        // Redirect to the long URL
        response.sendRedirect(affiliateLink.getLongUrl());
    }
}
