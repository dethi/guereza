package com.epita.guereza;

import com.epita.eventbus.EventBusClient;
import com.epita.eventbus.EventMessage;
import com.epita.guereza.eventsourcing.Event;
import com.epita.guereza.eventsourcing.Reducer;
import com.epita.guereza.service.CrawlerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public class UrlStore implements Repo, Reducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerService.class);

    private final EventBusClient eventBus;

    private Set<String> urlDone = new LinkedHashSet<>();
    private Set<String> urlTodo = new LinkedHashSet<>();

    public UrlStore(final EventBusClient eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void store(String[] urls) {
        for (String url : urls) {
            if (url == null || url.isEmpty())
                continue;

            if (!urlDone.contains(url))
                urlTodo.add(url);
        }
    }

    @Override
    public String nextUrl() {
        if (!urlTodo.isEmpty()) {
            // There is still
            String url = urlTodo.iterator().next();
            urlTodo.remove(url);
            urlDone.add(url);
            LOGGER.info("Repo still contains {} links", urlTodo.size());
            return url;
        }
        LOGGER.warn("No more url to analyse.");
        return null;
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

    private void addUrls(Event<String[]> event) {
        store(event.obj);
        LOGGER.info("added URLs to the repo");
    }

    private void crawlerRequestUrl(Event<String> event) {
        try {
            eventBus.publish(new EventMessage(event.obj, nextUrl()));
        } catch (JsonProcessingException e) {
            LOGGER.error("cannot serialize: {}", e.getMessage());
        }
    }

    private void indexerRequestUrl(Event<String> event) {
        try {
            eventBus.publish(new EventMessage(event.obj, nextUrl()));
        } catch (JsonProcessingException e) {
            LOGGER.error("cannot serialize: {}", e.getMessage());
        }
    }
}
