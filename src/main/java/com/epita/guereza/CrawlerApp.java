package com.epita.guereza;

import com.epita.domain.Crawler;
import com.epita.domain.RawDocument;
import com.epita.eventbus.EventBusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlerApp extends App {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerApp.class);
    private final Crawler crawler;
    private final String subscribeUrl;

    public CrawlerApp(final EventBusClient eventBus, final Crawler crawler) {
        super(eventBus);
        this.crawler = crawler;
        this.subscribeUrl = "/request/index/url/" + uid;
    }

    private String[] crawlAndExtract(final String url) {
        final RawDocument doc = crawler.crawl(url);
        if (doc == null)
            return null;

        return crawler.extractUrl(doc);
    }

    private void requestNextUrl() {
        LOGGER.info("Request next URL");
        sendMessage("/request/crawler/url", subscribeUrl);
    }

    private void storeUrls(final String[] urls) {
        LOGGER.info("Store {} urls", urls.length);
        sendMessage("/store/crawler", urls);
    }


    @Override
    public void run() {
        eventBus.subscribe(subscribeUrl, c -> {
            System.out.println(c.getContent());
            final String[] urls = crawlAndExtract(c.getContent());
            storeUrls(urls);

            requestNextUrl();
        });

        requestNextUrl();
    }
}