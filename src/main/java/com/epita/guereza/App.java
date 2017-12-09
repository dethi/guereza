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

    /**
     * The main stuff
     */
    public abstract void run();

    /**
     * Send an object through the eventBus
     *
     * @param channel The channel to send message
     * @param obj     The object to send
     */
    public void sendMessage(final String channel, final Object obj) {
        try {
            final EventMessage em = new EventMessage(channel, obj);
            eventBus.publish(em);
            LOGGER.info("Requesting next url");
        } catch (JsonProcessingException e) {
            LOGGER.error("Impossible to send message: {}", e.getMessage());
        }
    }
}