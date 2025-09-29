package com.marketer.affiliate_agent.service.network;

public interface AffiliateNetwork {
    boolean isApplicable(String url);
    String transform(String url);
}
