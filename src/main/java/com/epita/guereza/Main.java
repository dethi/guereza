package com.epita.guereza;

import com.epita.domain.Crawler;
import com.epita.domain.Document;
import com.epita.domain.Index;
import com.epita.domain.Indexer;
import com.epita.eventbus.EventBusClient;
import com.epita.eventbus.NettyEventBusClient;
import com.epita.guereza.eventsourcing.EventStore;
import com.epita.guereza.service.CrawlerService;
import com.epita.guereza.service.indexer.IndexerService;
import com.epita.winter.Scope;
import com.epita.winter.provider.LazySingleton;
import com.epita.winter.provider.Prototype;
import com.epita.winter.provider.Singleton;
import com.sun.security.ntlm.Server;

import java.util.Map;
import java.util.function.Function;

import static java.lang.System.exit;

public class Main {
    private static final int NETTY_PORT = 8000;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("usage: ./bin [ crawler | indexer | store | server");
            exit(1);
        }

        final String module = args[0];
        final Scope scope = createScope();

        switch (module) {
            case "crawler":
                runCrawler(scope);
                break;
            case "indexer":
                runIndexer(scope);
                break;
            case "store":
                runStore(scope);
                break;
            case "server":
                runServer();
                break;
        }
    }

    private static Scope createScope() {
        return new Scope()
                .register(new Singleton<>(Crawler.class, new CrawlerService()))
                .register(new Singleton<>(Indexer.class, new IndexerService()))
                .register(new Singleton<>(EventBusClient.class, new NettyEventBusClient()));
    }

    private static void runCrawler(Scope scope) {
        final Function<Scope, App> newCrawlerApp = (s) -> new CrawlerApp(
                s.instanceOf(EventBusClient.class), s.instanceOf(Crawler.class));

        scope.scope()
                .register(new Prototype<>(App.class, newCrawlerApp))
                .block(Main::runApp);
    }

    private static void runIndexer(Scope scope) {
        final Function<Scope, App> newIndexerApp = (s) -> new IndexerApp(
                s.instanceOf(EventBusClient.class), s.instanceOf(Indexer.class), s.instanceOf(Crawler.class));

        scope.scope()
                .register(new Prototype<>(App.class, newIndexerApp))
                .block(Main::runApp);
    }

    private static void runStore(Scope scope) {
        final Function<Scope, UrlStore> newUrlStore = (s) -> new UrlStore(s.instanceOf(EventBusClient.class));
        final Function<Scope, App> newEventStoreApp = (s) -> new EventStoreApp(
                s.instanceOf(EventBusClient.class), s.instanceOf(EventStore.class));

        scope.scope()
                .register(new Singleton<>(EventStore.class, new EventStore()))
                .register(new LazySingleton<>(UrlStore.class, newUrlStore))
                .register(new Prototype<>(App.class, newEventStoreApp))
                .block(Main::runApp);
    }

    private static void runApp(Scope scope) {
        App app = scope.instanceOf(App.class);
        app.run();
    }

    private static void runServer() {
        new ServerApp(NETTY_PORT).run();
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
