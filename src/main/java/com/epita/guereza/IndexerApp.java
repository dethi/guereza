package com.epita.guereza;

import com.epita.domain.Crawler;
import com.epita.domain.Document;
import com.epita.domain.Indexer;
import com.epita.domain.RawDocument;
import com.epita.eventbus.EventBusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.*;

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

    private void retryIn(final int seconds) {
        LOGGER.info("Retry fetching url in {}seconds", seconds);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(this::requestNextUrl, seconds, TimeUnit.SECONDS);
        executor.shutdownNow();
    }

    @Override
    public void run() {
        eventBus.subscribe(subscribeUrl, msg -> {
            if (msg != null) {
                final String url = msg.getContent();
                LOGGER.info("Receive url: {}", url);
                indexAndPublish(url);

                requestNextUrl();
            } else {
                retryIn(30);
            }
        });

        requestNextUrl();
    }
}
