package com.epita.guereza;

import com.epita.domain.*;
import com.epita.eventbus.EventBusClient;
import com.epita.eventbus.EventMessage;
import com.epita.eventbus.NettyEventBusClient;
import com.epita.eventbus.NettyServer;
import com.epita.guereza.service.CrawlerService;
import com.epita.guereza.service.indexer.IndexerService;
import com.epita.winter.Scope;
import com.epita.winter.provider.Prototype;
import com.epita.winter.provider.Singleton;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

import static com.epita.winter.Scope.getMethod;

public class Main {
    private static final String NETTY_HOST = "localhost";
    private static final int NETTY_PORT = 8000;

    public static void main(String[] args) {
//        final Index index = new Index();
//        final Repo repo = new RepoStore();
//
//        //repo.store(new String[]{"https://www.bbc.co.uk/food/recipes/saladenicoise_6572"});
//
//        boolean server = false;
//        boolean client = true;
//        if (server) {
//            testEventBusClientSubscribe();
//        } else {
//            testServer();
//        }
//
//        //testSearch(index, "onions courgettes pepper");

        testApp();
    }

    private static void testApp() {
        final Crawler crawler = new CrawlerService();
        final Indexer indexer = new IndexerService();
        final Repo repo = new RepoStore();

        final Function<Scope, EventBusClient> newEventBus = (s) -> new NettyEventBusClient();
        final Function<Scope, CrawlerApp> newCrawlerApp = (s) -> new CrawlerApp(s.instanceOf(EventBusClient.class), s.instanceOf(Crawler.class));
        final Function<Scope, IndexerApp> newIndexerApp = (s) -> new IndexerApp(s.instanceOf(EventBusClient.class), s.instanceOf(Indexer.class),
                s.instanceOf(Crawler.class));

        new Scope()
                .register(new Singleton<>(Crawler.class, crawler))
                .register(new Singleton<>(Indexer.class, indexer))
                .register(new Singleton<>(Repo.class, repo))
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


    private static void testEventBusClientSubscribe() {
        final NettyEventBusClient nebc = new NettyEventBusClient();
        final boolean succeed = nebc.run(NETTY_HOST, NETTY_PORT);

        if (succeed) {
            final EventBusClient.Subscription s = nebc.subscribe("room", message -> {
                System.out.println("sub: " + message.getContent());
            });
            try {
                nebc.publish(new EventMessage("room", "Hi!"));
                nebc.publish(new EventMessage("game", "Salut mec"));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
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
