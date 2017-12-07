package com.epita.guereza.crawler;


public interface ICrawler {
    RawDocument crawl(final String url);

    String[] extractUrl(RawDocument doc);

    String extractText(RawDocument doc);
}
