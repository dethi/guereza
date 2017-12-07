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

        final Index index = new Index();
        final Repo repo = new Repo();

        repo.store(new String[]{ "https://www.bbc.co.uk/food/recipes/saladenicoise_6572" });
        testCrawl(repo);
        testIndexing(repo, index);
        testSearch(index, "onions courgettes pepper");
    }

    private static void testCrawl(final Repo repo) {
        CrawlerService crawler = new CrawlerService();

        int limit = 20;
        while (limit-- > 0) {
            final String url = repo.nextUrl();
            if (url == null)
                break;

            final RawDocument doc = crawler.crawl(url);
            if (doc == null)
                continue;

            final String[] urls = crawler.extractUrl(doc);
            repo.store(urls);
        }
    }

    private static void testIndexing(final Repo repo, final Index index) {
        IndexerService indexer = new IndexerService();

        String url = repo.nextUrl();
        int limit = 300;
        while (url != null && limit-- > 0) {
            final Document d = indexer.index(url);
            url = repo.nextUrl();

            if (d == null)
                continue;
            indexer.publish(index, d);
        }
    }

    private static void testSearch(final Index index, final String query) {
        IndexerService indexer = new IndexerService();

        System.out.printf("Results for '%s'\n", query);

        final Map<Document, Double> res = indexer.search(index.docs, query);
        for (final Map.Entry<Document, Double> doc : res.entrySet()) {
            System.out.printf("%100s %f\n", doc.getKey().url, doc.getValue());
        }
    }
}
