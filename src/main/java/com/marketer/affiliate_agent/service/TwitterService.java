package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

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
    public void post(String message) {
        try {
            twitter.updateStatus(message);
        } catch (TwitterException e) {
            throw new ApiException("Failed to post tweet to Twitter.", e);
        }
    }
}
