package com.epita.guereza;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Indexer {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    public Document index(String url) {
        logger.info("logging {}", url);
        Crawler c = new Crawler();
        RawDocument d = c.crawl(url);
        String text = c.extractText(d);


        String[] words = text.split("\\s+");
        return null;
    }

    public void publish(Document d) {

    }
}
