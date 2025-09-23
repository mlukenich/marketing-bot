package com.marketer.affiliate_agent.service;

import org.springframework.stereotype.Service;

@Service
public class FacebookService implements SocialMediaService {

    @Override
    public void post(String message) {
        // In a real application, you would use a Facebook API client here (e.g., RestFB).
        // This is a placeholder for the actual API call.
        System.out.println("--- Posting to Facebook ---");
        System.out.println(message);
        System.out.println("---------------------------");
    }
}
