package com.epita.guereza.crawler;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Crawler implements ICrawler {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    @Override
    public RawDocument crawl(final String url) {
        try {
            logger.info("crawling {}", url);
            return new RawDocument(Jsoup.connect(url).get());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public String[] extractUrl(final RawDocument d) {
        final Elements elts = d.getDoc().select("a");
        return elts.stream().map((e) -> e.absUrl("href")).toArray(String[]::new);
    }

    @Override
    public String extractText(final RawDocument d) {
        return d.getDoc().text();
    }
}
