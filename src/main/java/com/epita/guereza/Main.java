package com.epita.guereza;

import com.epita.domain.Crawler;
import com.epita.domain.Document;
import com.epita.domain.Index;
import com.epita.domain.Indexer;
import com.epita.eventbus.EventBusClient;
import com.epita.eventbus.NettyEventBusClient;
import com.epita.eventbus.NettyServer;
import com.epita.guereza.service.CrawlerService;
import com.epita.guereza.service.indexer.IndexerService;
import com.epita.winter.Scope;
import com.epita.winter.provider.LazySingleton;
import com.epita.winter.provider.Prototype;
import com.epita.winter.provider.Singleton;

import java.util.Map;
import java.util.function.Function;

public class Main {
    private static final int NETTY_PORT = 8000;

    public static void main(String[] args) {
        final Crawler crawler = new CrawlerService();
        final Indexer indexer = new IndexerService();

        final Function<Scope, EventBusClient> newEventBus = (s) -> new NettyEventBusClient();
        final Function<Scope, CrawlerApp> newCrawlerApp = (s) -> new CrawlerApp(s.instanceOf(EventBusClient.class), s.instanceOf(Crawler.class));
        final Function<Scope, IndexerApp> newIndexerApp = (s) -> new IndexerApp(s.instanceOf(EventBusClient.class), s.instanceOf(Indexer.class),
                s.instanceOf(Crawler.class));
        final Function<Scope, Repo> newRepo = (s) -> new UrlStore(s.instanceOf(EventBusClient.class));

        new Scope()
                .register(new Singleton<>(Crawler.class, crawler))
                .register(new Singleton<>(Indexer.class, indexer))
                .register(new LazySingleton<>(Repo.class, newRepo))
                .register(new Prototype<>(EventBusClient.class, newEventBus))
                .register(new Prototype<>(CrawlerApp.class, newCrawlerApp))
                .register(new Prototype<>(IndexerApp.class, newIndexerApp))
                .block(Main::runApp);
    }

    private static void runApp(Scope scope) {
        CrawlerApp crawlerApp = scope.instanceOf(CrawlerApp.class);
        crawlerApp.run();
    }

    private static void testServer() {
        final NettyServer ns = new NettyServer();
        ns.run(NETTY_PORT);
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
