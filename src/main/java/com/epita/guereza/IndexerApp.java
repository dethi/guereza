package com.epita.guereza;

import com.epita.domain.Crawler;
import com.epita.domain.Document;
import com.epita.domain.Indexer;
import com.epita.eventbus.EventBusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexerApp extends App {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexerApp.class);
    private final Indexer indexer;
    private final Crawler crawler;

    public IndexerApp(final Indexer indexer, final Crawler crawler, final EventBusClient eventBus) {
        super(eventBus);

        this.indexer = indexer;
        this.crawler = crawler;
    }

    private void publishDocument(final Document doc) {
        if (doc != null) {
            // FIXME: publish

            //indexer.publish(index, doc);
        }
    }

    private void requestNextUrl() {
        sendMessage("/request/indexer/url", uid);
    }

    @Override
    public void run() {
        eventBus.subscribe("/request/index/url/" + uid, msg -> {
            final String url = msg.getContent();
            LOGGER.info("Receive url: {}", url);
            publishDocument(indexer.index(url));

            requestNextUrl();
        });

        requestNextUrl();
    }
}
