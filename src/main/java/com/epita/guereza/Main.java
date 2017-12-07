package com.epita.guereza;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.debug("Starting crawler");


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

        /*
        String[] words = new String[] {
                        "consign",
                        "consigned",
                        "consigning",
                        "consignment",
                        "consist",
                        "consisted",
                        "consistency",
                        "consistent",
                        "consistently",
                        "consisting",
                        "consists",
                        "consolation",
                        "consolations",
                        "consolatory",
                        "console",
                        "consoled",
                        "consoles",
                        "consolidate",
                        "consolidated",
                        "consolidating",
                        "consoling",
                        "consolingly",
                        "consols",
                        "consonant",
                        "consort",
                        "consorted",
                        "consorting",
                        "conspicuous",
                        "conspicuously",
                        "conspiracy",
                        "conspirator",
                        "conspirators",
                        "conspire",
                        "conspired",
                        "conspiring",
                        "constable",
                        "constables",
                        "constance",
                        "constancy",
                        "constant",
                        "knottlaa",
                        "knottly",
                        "knott"
        };
        for (String w: words) {
            System.out.println(indexer.stemmed(w));
        }
        */

        //Crawler crawler = new Crawler();

        /*
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
        */
    }
}
