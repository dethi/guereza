package com.epita.guereza;

import com.epita.domain.Crawler;
import com.epita.domain.Indexer;
import com.epita.domain.SimpleCrawler;
import com.epita.domain.SimpleIndexer;
import com.epita.eventbus.client.EventBusClient;
import com.epita.eventbus.client.NettyEventBusClient;
import com.epita.eventsourcing.EventStore;
import com.epita.guereza.application.*;
import com.epita.guereza.reducer.RetroIndex;
import com.epita.guereza.reducer.UrlStore;
import com.epita.winter.Scope;
import com.epita.winter.provider.LazySingleton;
import com.epita.winter.provider.Prototype;
import com.epita.winter.provider.Singleton;

import java.util.function.Function;

import static java.lang.System.exit;

public class Main {
    private static final int NETTY_PORT = 8000;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("usage: ./bin [crawler | indexer | store | server] SERVER_HOST");
            exit(1);
        }

        final String module = args[0];
        final Scope scope = createScope(args[1], NETTY_PORT);

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

    private static Scope createScope(final String host, final int port) {
        return new Scope()
                .register(new Singleton<>(Crawler.class, new SimpleCrawler()))
                .register(new Singleton<>(Indexer.class, new SimpleIndexer()))
                .register(new Singleton<>(EventBusClient.class, new NettyEventBusClient(host, port)));
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
                .register(new LazySingleton<>(UrlStore.class, newUrlStore))
                .register(new Singleton<>(RetroIndex.class, new RetroIndex()))
                .register(new LazySingleton<>(EventStore.class, (s) -> new EventStore())
                        .afterCreate((s, obj) -> {
                            obj.addReducer(s.instanceOf(UrlStore.class));
                            obj.addReducer(s.instanceOf(RetroIndex.class));
                        }))
                .register(new Prototype<>(App.class, newEventStoreApp))
                .block(Main::runApp);
    }

    private static void runApp(Scope scope) {
        boolean ok = scope.instanceOf(EventBusClient.class).start();
        if (ok) {
            scope.instanceOf(App.class).run();
        }
    }

    private static void runServer() {
        new ServerApp(NETTY_PORT).run();
    }
}
