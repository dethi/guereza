package com.epita.eventbus;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class EventSubscription implements EventBusClient.Subscription {
    private final EventBusClient.Channel channel;
    private final Consumer<EventBusClient.Message> callback;
    private final LocalDateTime subscriptionDateTime;
    private long messageReceivedCount;

    public EventSubscription(final EventBusClient.Channel channel, final Consumer<EventBusClient.Message> callback) {
        this.channel = channel;
        this.callback = callback;
        this.subscriptionDateTime = LocalDateTime.now();
        this.messageReceivedCount = 0;
    }

    @Override
    public EventBusClient.Channel getChannel() {
        return channel;
    }

    @Override
    public Consumer<EventBusClient.Message> getCallback() {
        this.messageReceivedCount++;
        return callback;
    }

    @Override
    public LocalDateTime getSubscriptionDateTime() {
        return subscriptionDateTime;
    }

    @Override
    public long getMessageReceivedCount() {
        return this.messageReceivedCount;
    }
}