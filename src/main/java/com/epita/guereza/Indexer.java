package com.epita.guereza;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Indexer {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    public Document index(String url) {
        logger.info("logging {}", url);
        Crawler c = new Crawler();
        RawDocument d = c.crawl(url);
        String text = c.extractText(d);


        String[] sentences = getSentences(text);
        for (String s : sentences) {
            System.out.println(s);
            System.out.println("------");
        }
        return null;
    }

    String[] getSentences(String text) {
        return Arrays.stream(text.split("[.!?]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    public void publish(Document d) {

    }
}
