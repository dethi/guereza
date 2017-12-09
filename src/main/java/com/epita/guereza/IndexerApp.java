package com.epita.guereza;

import com.epita.domain.Crawler;
import com.epita.domain.Document;
import com.epita.domain.Indexer;
import com.epita.domain.RawDocument;
import com.epita.eventbus.EventBusClient;
import com.epita.guereza.service.CrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexerApp extends App {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexerApp.class);
    private final Indexer indexer;
    private final Crawler crawler;

    public IndexerApp(final EventBusClient eventBus, final Indexer indexer, final Crawler crawler) {
        super(eventBus);

        this.indexer = indexer;
        this.crawler = crawler;
    }

    private void indexAndPublish(final String url) {
        final CrawlerService c = new CrawlerService();
        final RawDocument d = c.crawl(url);
        if (d == null)
            return;

        final String text = c.extractText(d);
        final Document doc = indexer.index(text, url);
        if (doc != null) {
            // FIXME: publish

            //indexer.publish(index, doc);
            sendMessage("/store/indexer", doc);
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
            indexAndPublish(url);

            requestNextUrl();
        });

        requestNextUrl();
    }
}
