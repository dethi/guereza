package com.epita.guereza;

import com.epita.domain.Crawler;
import com.epita.domain.Indexer;
import com.epita.eventbus.EventBusClient;

import java.util.UUID;

public class IndexerApp {
    public final String uid;

    public final Indexer indexer;
    public final Crawler crawler;
    public final EventBusClient eventBus;

    public IndexerApp(final Indexer indexer, final Crawler crawler, final EventBusClient eventBus) {
        this.uid = UUID.randomUUID().toString();

        this.indexer = indexer;
        this.crawler = crawler;
        this.eventBus = eventBus;
    }
}
