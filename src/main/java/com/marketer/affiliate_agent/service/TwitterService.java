package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.entity.AffiliateLink;
import com.marketer.affiliate_agent.entity.GeneratedContent;
import com.marketer.affiliate_agent.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

@Service
public class TwitterService implements SocialMediaService {

    private final Twitter twitter;

    public TwitterService(@Value("${twitter.oauth.consumerKey}") String consumerKey,
                          @Value("${twitter.oauth.consumerSecret}") String consumerSecret,
                          @Value("${twitter.oauth.accessToken}") String accessToken,
                          @Value("${twitter.oauth.accessTokenSecret}") String accessTokenSecret) {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        this.twitter = tf.getInstance();
    }

    @Override
    public void post(AffiliateLink link, String trackableUrl) {
        List<GeneratedContent> contentVariations = link.getGeneratedContent();
        if (contentVariations == null || contentVariations.isEmpty()) {
            System.err.println("Skipping Twitter post for link ID: " + link.getId() + " because no content is available.");
            return;
        }

        // Default to posting the first content variation
        String content = contentVariations.get(0).getContent();
        String message = content + "\n" + trackableUrl;

        try {
            twitter.updateStatus(message);
        } catch (TwitterException e) {
            throw new ApiException("Failed to post tweet to Twitter.", e);
        }
    }
}
