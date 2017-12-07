package com.epita.guereza.domain;


public interface Crawler {
    RawDocument crawl(final String url);

    String[] extractUrl(RawDocument doc);

    String extractText(RawDocument doc);
}
