package com.epita.guereza;

import com.epita.guereza.crawler.Crawler;
import com.epita.guereza.crawler.RawDocument;
import com.epita.guereza.indexer.Document;
import com.epita.guereza.indexer.Index;
import com.epita.guereza.indexer.Indexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.debug("Starting crawler");

        //testTfIdf();
        testCrawl("https://en.wikipedia.org/wiki/Halifax_Explosion");
    }

    private static void testTfIdf() {
        Index index = new Index();
        Repo repo = new Repo();


        repo.store(new String[]{
                "https://en.wikipedia.org/wiki/Halifax_Explosion",
                "http://www.midwestliving.com/food/fruits-veggies/40-fresh-tomato-recipes-youll-love/",
                "http://www.health.com/health/gallery/0,,20723744,00.html",
                "http://www.delish.com/cooking/g1448/quick-easy-tomato-recipes/",
                "https://www.bbc.co.uk/food/recipes/saladenicoise_6572"
        });
        Indexer indexer = new Indexer();

        String url = repo.nextUrl();
        while (url != null) {
            Document d = indexer.index(url);
            if (d == null)
                continue;
            indexer.publish(index, d);
            url = repo.nextUrl();
        }

        HashMap<Document, Double> res = indexer.search(index.getDocs(), "tomatoes");
        for (Map.Entry<Document, Double> doc: res.entrySet()) {
            System.out.printf("%100s %f\n", doc.getKey().getUrl(), doc.getValue());
        }
    }

    private static void testCrawl(final String startUrl) {
        Repo repo = new Repo();
        repo.store(new String[]{ startUrl });
        Crawler crawler = new Crawler();

        while (true) {
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
