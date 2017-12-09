package com.epita.guereza.application;

import com.epita.domain.Crawler;
import com.epita.domain.Document;
import com.epita.domain.Indexer;
import com.epita.domain.RawDocument;
import com.epita.eventbus.client.EventBusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexerApp extends App {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexerApp.class);

    private final Indexer indexer;
    private final Crawler crawler;
    private final String subscribeUrl;

    public IndexerApp(final EventBusClient eventBus, final Indexer indexer, final Crawler crawler) {
        super(eventBus);

        this.indexer = indexer;
        this.crawler = crawler;
        this.subscribeUrl = "/request/index/url/" + uid;
    }

    private void indexAndPublish(final String url) {
        final RawDocument d = crawler.crawl(url);
        if (d == null)
            return;

        final String text = crawler.extractText(d);
        final Document doc = indexer.index(text, url);
        if (doc != null) {
            LOGGER.info("Store document");
            sendMessage("/store/indexer", doc);
        }
    }

    private void requestNextUrl() {
        LOGGER.info("Request next URL");
        sendMessage("/request/indexer/url", subscribeUrl);
    }

    @Override
    public void run() {
        eventBus.subscribe(subscribeUrl, msg -> {
            if (msg != null) {
                final String url = (String) mappingObject(msg);
                if (url != null) {
                    LOGGER.info("Receive url: {}", url);
                    indexAndPublish(url);
                }

                requestNextUrl();
            } else {
                retryIn(30, this::requestNextUrl);
            }
        });

        requestNextUrl();
    }
}
