package com.epita.guereza;

import com.epita.domain.Crawler;
import com.epita.eventbus.EventBusClient;

import java.util.UUID;

public class CrawlerApp {
    public final String uid;

    public final Crawler crawler;
    public final Repo repo;
    public final EventBusClient eventBus;

    public CrawlerApp(final Crawler crawler, final Repo repo, final EventBusClient eventBus) {
        this.uid = UUID.randomUUID().toString();

        this.crawler = crawler;
        this.repo = repo;
        this.eventBus = eventBus;
    }
}
