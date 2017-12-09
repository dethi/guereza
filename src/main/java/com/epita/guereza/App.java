package com.epita.guereza;

import com.epita.eventbus.EventBusClient;
import com.epita.eventbus.EventMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public abstract class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    protected final String uid;
    protected final EventBusClient eventBus;

    protected App(final EventBusClient eventBus) {
        this.uid = UUID.randomUUID().toString();
        this.eventBus = eventBus;
    }

    public abstract void run();

    public void sendMessage(final String channel, final Object obj) {
        try {
            final EventMessage em = new EventMessage(channel, obj);
            eventBus.publish(em);
            LOGGER.info("Requesting next url");
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot serialize message: {}", e.getMessage());
        }
    }
}
