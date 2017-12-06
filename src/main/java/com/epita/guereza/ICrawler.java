package com.epita.guereza;


public interface ICrawler {
    RawDocument crawl(final String url);

    String[] extractUrl(RawDocument doc);

    String extractText(RawDocument doc);
}
