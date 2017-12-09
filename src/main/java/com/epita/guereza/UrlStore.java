package com.epita.guereza;

import com.epita.eventbus.EventBusClient;
import com.epita.eventbus.EventMessage;
import com.epita.guereza.eventsourcing.Event;
import com.epita.guereza.eventsourcing.Reducer;
import com.epita.guereza.service.CrawlerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class UrlStore implements Reducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerService.class);

    private final EventBusClient eventBus;

    private Set<String> allUrls = new HashSet<>();
    private Queue<String> crawlerTodo = new LinkedList<>();
    private Queue<String> indexerTodo = new LinkedList<>();

    public UrlStore(final EventBusClient eventBus) {
        this.eventBus = eventBus;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reduce(final Event<?> event) {
        switch (event.type) {
            case "ADD_URLS":
                addUrls((Event<String[]>) event);
                break;
            case "CRAWLER_REQUEST_URL":
                crawlerRequestUrl((Event<String>) event);
                break;
            case "INDEXER_REQUEST_URL":
                indexerRequestUrl((Event<String>) event);
                break;
        }
    }

    private void store(String[] urls) {
        for (String url : urls) {
            if (url == null || url.isEmpty())
                continue;

            if (!allUrls.contains(url)) {
                allUrls.add(url);
                crawlerTodo.add(url);
                indexerTodo.add(url);
            }
        }
    }

    private void addUrls(Event<String[]> event) {
        store(event.obj);
        LOGGER.info("added URLs to the repo");
    }

    private void crawlerRequestUrl(Event<String> event) {
        try {
            LOGGER.info("Still {} urls to crawl", crawlerTodo.size());
            eventBus.publish(new EventMessage(event.obj, crawlerTodo.poll()));
        } catch (JsonProcessingException e) {
            LOGGER.error("cannot serialize: {}", e.getMessage());
        }
    }

    private void indexerRequestUrl(Event<String> event) {
        try {
            LOGGER.info("Still {} urls to index", indexerTodo.size());
            eventBus.publish(new EventMessage(event.obj, indexerTodo.poll()));
        } catch (JsonProcessingException e) {
            LOGGER.error("cannot serialize: {}", e.getMessage());
        }
    }
}
