package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.exception.ApiException;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookException;
import com.restfb.types.GraphResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FacebookService implements SocialMediaService {

    private final String pageId;
    private final FacebookClient facebookClient;

    public FacebookService(@Value("${facebook.page.id}") String pageId,
                           @Value("${facebook.page.accessToken}") String pageAccessToken) {
        this.pageId = pageId;
        this.facebookClient = new DefaultFacebookClient(pageAccessToken, Version.LATEST);
    }

    @Override
    public void post(AffiliateLink link, String trackableUrl) {
        // For Facebook, we can combine the message and the link in the 'message' parameter.
        // A more advanced implementation could use the 'link' parameter for a link preview.
        String message = link.getGeneratedContent() + "\n\n" + trackableUrl;

        try {
            String postEndpoint = "/" + pageId + "/feed";

            GraphResponse publishResponse = facebookClient.publish(postEndpoint, GraphResponse.class,
                    Parameter.with("message", message));

            System.out.println("Successfully posted to Facebook. Post ID: " + publishResponse.getId());

        } catch (FacebookException e) {
            throw new ApiException("Failed to post to Facebook: " + e.getMessage(), e);
        }
    }
}
