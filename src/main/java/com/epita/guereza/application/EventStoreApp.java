package com.epita.guereza.application;

import com.epita.domain.Document;
import com.epita.eventbus.client.EventBusClient;
import com.epita.eventsourcing.Event;
import com.epita.eventsourcing.EventStore;
import com.epita.guereza.StringListWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class EventStoreApp extends App {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventStore.class);

    private final EventStore eventStore;

    public EventStoreApp(final EventBusClient eventBus, final EventStore eventStore) {
        super(eventBus);

        this.eventStore = eventStore;
    }

    private void extractThen(final EventBusClient.Message msg, final Consumer<Object> consumer) {
        try {
            LOGGER.info("Message Type: {}", msg.getMessageType());
            final Class c = ClassLoader.getSystemClassLoader().loadClass(msg.getMessageType());
            final Object o = new ObjectMapper().readValue(msg.getContent(), c);
            if (o != null) {
                consumer.accept(o);
            }
        } catch (final Exception e) {
            LOGGER.error("Impossible to extract object from message: {}", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        eventBus.subscribe("/request/crawler/url", msg -> extractThen(msg, o -> {
            final Event<String> ev = new Event<>("CRAWLER_REQUEST_URL", (String) o);
            eventStore.dispatch(ev);
        }));
        eventBus.subscribe("/request/indexer/url", msg -> extractThen(msg, o -> {
            final Event<String> ev = new Event<>("INDEXER_REQUEST_URL", (String) o);
            eventStore.dispatch(ev);
        }));
        eventBus.subscribe("/store/crawler", msg -> extractThen(msg, o -> {
            final Event<StringListWrapper> ev = new Event<>("ADD_URLS", (StringListWrapper) o);
            eventStore.dispatch(ev);
        }));
        eventBus.subscribe("/store/indexer", msg -> extractThen(msg, o -> {
            final Event<Document> ev = new Event<>("ADD_DOCUMENT", (Document) o);
            eventStore.dispatch(ev);
        }));
    }
}
