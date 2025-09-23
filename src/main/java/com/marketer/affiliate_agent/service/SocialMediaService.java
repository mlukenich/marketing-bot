package com.marketer.affiliate_agent.service;

import com.marketer.affiliate_agent.entity.AffiliateLink;

public interface SocialMediaService {
    void post(AffiliateLink link, String trackableUrl);
}
