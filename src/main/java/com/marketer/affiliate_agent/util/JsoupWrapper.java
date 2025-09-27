package com.marketer.affiliate_agent.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
public class JsoupWrapper {
    public Connection connect(String url) {
        return Jsoup.connect(url);
    }
}
