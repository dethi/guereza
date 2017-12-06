package com.epita.guereza;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Crawler implements ICrawler {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    public Document crawl(final String url) {
        try {
            return Jsoup.connect("http://google.com/").get();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public String[] extractUrl(Document doc) {
        Elements elts = doc.select("a");
        return elts.stream().map((e) -> e.absUrl("href")).toArray(String[]::new);
    }

    public String extractText(Document doc) {
        return doc.text();
    }
}
