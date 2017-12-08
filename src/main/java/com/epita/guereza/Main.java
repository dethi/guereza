package com.epita.guereza;

import com.epita.guereza.domain.Document;
import com.epita.guereza.domain.Index;
import com.epita.guereza.domain.Indexer;
import com.epita.guereza.domain.RawDocument;
import com.epita.guereza.indexer.IndexerService;
import com.epita.guereza.winter.Scope;
import com.epita.guereza.winter.provider.Prototype;
import com.epita.guereza.winter.provider.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

import static com.epita.guereza.winter.Scope.getMethod;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        final Index index = new Index();
        final Repo repo = new RepoStore();

        repo.store(new String[]{"https://www.bbc.co.uk/food/recipes/saladenicoise_6572"});
        testWinter(repo);
        //testCrawl(repo);
        //testIndexing(repo, index);
        //testSearch(index, "onions courgettes pepper");
    }

    private static void testWinter(final Repo repo) {
        Method method = getMethod(Repo.class, "nextUrl");
        new Scope()
                .register(
                        new Singleton<>(Repo.class, repo)
                                .before(method, (s, obj) -> System.out.println("before::"))
                                .after(method, (s, obj) -> System.out.println("after::"))
                                .around(method, (ctx) -> {
                                    System.out.println("beforeAround::");
                                    Object obj = ctx.call();
                                    System.out.println("afterAround::");
                                    return obj;
                                })
                                .beforeDestroy((s, obj) -> System.out.println("destroy::")))
                .register(
                        new Prototype<>(Indexer.class, (s) -> new IndexerService())
                                .afterCreate((s, obj) -> System.out.println("beforeCreate::"))
                                .beforeDestroy((s, obj) -> System.out.println("destroy::"))
                )
                .block((s) -> {
                    Repo r = s.instanceOf(Repo.class);
                    System.out.println(r.nextUrl());

                    s.instanceOf(Indexer.class);
                    s.instanceOf(Indexer.class);
                    s.instanceOf(Indexer.class);
                });
    }

    private static void testCrawl(final Repo repo) {
        CrawlerService crawler = new CrawlerService();

        int limit = 20;
        while (limit-- > 0) {
            final String url = repo.nextUrl();
            if (url == null)
                break;

            final RawDocument doc = crawler.crawl(url);
            if (doc == null)
                continue;

            final String[] urls = crawler.extractUrl(doc);
            repo.store(urls);
        }
    }

    private static void testIndexing(final Repo repo, final Index index) {
        IndexerService indexer = new IndexerService();

        String url = repo.nextUrl();
        int limit = 300;
        while (url != null && limit-- > 0) {
            final Document d = indexer.index(url);
            url = repo.nextUrl();

            if (d == null)
                continue;
            indexer.publish(index, d);
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
