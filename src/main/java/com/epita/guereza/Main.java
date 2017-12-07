package com.epita.guereza;

import com.epita.guereza.domain.Document;
import com.epita.guereza.domain.Index;
import com.epita.guereza.domain.RawDocument;
import com.epita.guereza.indexer.IndexerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.debug("Starting crawler");

        Repo repo = new Repo();
        repo.store(new String[]{ "https://www.bbc.co.uk/food/recipes/saladenicoise_6572" });
        testCrawl(repo, 40);
        testTfIdf(repo, "tomatoes");
    }

    private static void testTfIdf(final Repo repo, final String query) {
        Index index = new Index();
        IndexerService indexer = new IndexerService();

        String url = repo.nextUrl();
        while (url != null) {
            Document d = indexer.index(url);
            url = repo.nextUrl();

            if (d == null)
                continue;
            indexer.publish(index, d);
        }

        Map<Document, Double> res = indexer.search(index.docs, query);
        for (Map.Entry<Document, Double> doc : res.entrySet()) {
            System.out.printf("%100s %f\n", doc.getKey().url, doc.getValue());
        }
    }

    private static void testCrawl(final Repo repo, final int limit) {
        CrawlerService crawler = new CrawlerService();

        int i = 0;
        while (i++ < limit) {
            String url = repo.nextUrl();
            if (url == null)
                break;

            RawDocument doc = crawler.crawl(url);
            if (doc == null)
                continue;

            String[] urls = crawler.extractUrl(doc);
            repo.store(urls);
        }
    }
}
