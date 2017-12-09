package com.epita.guereza;

import com.epita.domain.Document;
import com.epita.eventbus.EventBusClient;
import com.epita.eventbus.EventMessage;
import com.epita.guereza.eventsourcing.Event;
import com.epita.guereza.eventsourcing.EventStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class EventStoreApp extends App {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventStore.class);
    private final EventStore eventStore;

    protected EventStoreApp(EventBusClient eventBus, EventStore eventStore) {
        super(eventBus);

        this.eventStore = eventStore;
    }

    private void extractThen(final EventBusClient.Message msg, final Consumer<Object> consumer) {
        try {
            Class c = ClassLoader.getSystemClassLoader().loadClass(msg.getMessageType());
            Object o = new ObjectMapper().readValue(msg.getContent(), c);
            if (o != null) {
                consumer.accept(o);
            }
        } catch (Exception e) {
            LOGGER.error("Impossible to extract object from message: {}", e);
        }
    }

    @Override
    public void run() {
        eventBus.subscribe("/request/crawler/url", msg -> {
            extractThen(msg, o -> {
                Event<String> ev = new Event<>("CRAWLER_REQUEST_URL", String.class, (String)o);
                eventStore.dispatch(ev);
            });
        });
        eventBus.subscribe("/request/indexer/url", msg -> {
            extractThen(msg, o -> {
                Event<String> ev = new Event<>("INDEXER_REQUEST_URL", String.class, (String)o);
                eventStore.dispatch(ev);
            });
        });
        eventBus.subscribe("/store/crawler", msg -> {
            extractThen(msg, o -> {
                Event<String[]> ev = new Event<>("ADD_URLS", String[].class, (String[])o);
                eventStore.dispatch(ev);
            });
        });
        eventBus.subscribe("/store/indexer", msg -> {
            extractThen(msg, o -> {
                Event<Document> ev = new Event<>("ADD_DOCUMENT", Document.class, (Document)o);
                eventStore.dispatch(ev);
            });
        });
    }
}
