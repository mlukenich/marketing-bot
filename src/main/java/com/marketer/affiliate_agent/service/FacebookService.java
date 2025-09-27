package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.GeneratedContent;
import com.marketer.affiliate_agent.exception.ApiException;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookException;
import com.restfb.types.GraphResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacebookService implements SocialMediaService {

    private static final Logger log = LoggerFactory.getLogger(FacebookService.class);

    private final String pageId;
    private final FacebookClient facebookClient;

    public FacebookService(@Value("${facebook.page.id}") String pageId,
                           @Value("${facebook.page.accessToken}") String pageAccessToken) {
        this.pageId = pageId;
        this.facebookClient = new DefaultFacebookClient(pageAccessToken, Version.LATEST);
    }

    @Override
    public void post(AffiliateLink link, String trackableUrl) {
        List<GeneratedContent> contentVariations = link.getGeneratedContent();
        if (contentVariations == null || contentVariations.isEmpty()) {
            log.warn("Skipping Facebook post for link ID: {} because no content is available.", link.getId());
            return;
        }

        String content = contentVariations.get(0).getContent();
        String message = content + "\n\n" + trackableUrl;

        try {
            String postEndpoint = "/" + pageId + "/feed";

            GraphResponse publishResponse = facebookClient.publish(postEndpoint, GraphResponse.class,
                    Parameter.with("message", message));

            log.info("Successfully posted to Facebook. Post ID: {}", publishResponse.getId());

        } catch (FacebookException e) {
            throw new ApiException("Failed to post to Facebook: " + e.getMessage(), e);
        }
    }
}
