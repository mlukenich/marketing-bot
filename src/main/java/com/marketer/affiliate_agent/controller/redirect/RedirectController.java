package com.marketer.affiliate_agent.controller.redirect;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.GeneratedContent;
import com.marketer.affiliate_agent.entity.LinkClick;
import com.marketer.affiliate_agent.exception.ApiException;
import com.marketer.affiliate_agent.repository.GeneratedContentRepository;
import com.marketer.affiliate_agent.repository.LinkClickRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

@Controller
public class RedirectController {

    private final GeneratedContentRepository generatedContentRepository;
    private final LinkClickRepository linkClickRepository;

    public RedirectController(GeneratedContentRepository generatedContentRepository, LinkClickRepository linkClickRepository) {
        this.generatedContentRepository = generatedContentRepository;
        this.linkClickRepository = linkClickRepository;
    }

    @GetMapping("/track/{contentId}")
    @Transactional
    public void redirectToLongUrl(@PathVariable Long contentId, HttpServletResponse response) throws IOException {
        GeneratedContent content = generatedContentRepository.findById(contentId)
                .orElseThrow(() -> new ApiException("Content not found for ID: " + contentId));

        // Increment click count on the specific content variation
        content.setClickCount(content.getClickCount() + 1);

        // Increment total click count on the parent link
        AffiliateLink parentLink = content.getAffiliateLink();
        parentLink.setClickCount(parentLink.getClickCount() + 1);

        // Record the new click event
        LinkClick click = new LinkClick();
        click.setGeneratedContent(content);
        linkClickRepository.save(click);

        // Note: We don't need to explicitly save the content or parentLink here
        // because they are managed entities within a @Transactional method.
        // The changes will be automatically persisted to the database upon successful completion.

        // Redirect to the final affiliate URL
        response.sendRedirect(parentLink.getLongUrl());
    }
}
