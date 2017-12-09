package com.epita.domain;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SimpleCrawler implements Crawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCrawler.class);

    @Override
    public RawDocument crawl(final String url) {
        try {
            LOGGER.info("crawling {}", url);
            return new RawDocument(Jsoup.connect(url).get());
        } catch (final IOException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Override
    public String[] extractUrl(final RawDocument d) {
        final Elements elts = d.doc.select("a");
        return elts.stream().map((e) -> e.absUrl("href")).toArray(String[]::new);
    }

    @Override
    public String extractText(final RawDocument d) {
        return d.doc.text();
    }
}
