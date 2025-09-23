package com.marketer.affiliate_agent.service;

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
    public void post(String message) {
        try {
            // The endpoint for posting to a page's feed is "/PAGE_ID/feed"
            String postEndpoint = "/" + pageId + "/feed";
            
            // Publish the post
            GraphResponse publishResponse = facebookClient.publish(postEndpoint, GraphResponse.class,
                    Parameter.with("message", message));

            System.out.println("Successfully posted to Facebook. Post ID: " + publishResponse.getId());

        } catch (FacebookException e) {
            // Wrap the Facebook-specific exception in our custom ApiException
            throw new ApiException("Failed to post to Facebook: " + e.getMessage(), e);
        }
    }
}
