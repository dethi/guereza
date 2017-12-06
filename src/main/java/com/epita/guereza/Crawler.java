package com.epita.guereza;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Crawler implements ICrawler {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    @Override
    public Document crawl(final String url) {
        try {
            logger.info("crawling {}", url);
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public String[] extractUrl(Document doc) {
        Elements elts = doc.select("a");
        return elts.stream().map((e) -> e.absUrl("href")).toArray(String[]::new);
    }

    @Override
    public String extractText(Document doc) {
        return doc.text();
    }
}
