package com.epita.eventbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NettyEventBusClient implements EventBusClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyEventBusClient.class);
    private final Map<String, List<Subscription>> subscriptionsMap;
    private io.netty.channel.Channel nettyChannel;
    private EventLoopGroup group;

    public NettyEventBusClient() {
        subscriptionsMap = new HashMap<>();
        group = new NioEventLoopGroup();
        nettyChannel = null;
    }

    /**
     * Start the EvenBusClient
     *
     * @return Whether or not the run succeed
     */
    public boolean run(final String host, final int port) {
        if (nettyChannel != null)
            return false;

        group = new NioEventLoopGroup();
        try {
            final Bootstrap bootstrap = new io.netty.bootstrap.Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyClientInitializer(this::trigger));

            nettyChannel = bootstrap.connect(host, port).sync().channel();
        } catch (Exception e) {
            LOGGER.error("Impossible to connect to {}:{}", host, port);
            return false;
        }
        LOGGER.info("Connected on {}:{}", host, port);

        return true;
    }

    /**
     * Close the EventBus
     */
    public void shutdown() {
        group.shutdownGracefully();
    }

    /**
     * Subscribe to a new netty channel
     *
     * @param channel  The channel to subscribe to.
     * @param callback The callback to register and have called on message reception.
     * @return The subscription object created
     */
    @Override
    public Subscription subscribe(final String channel, final Consumer<Message> callback) {
        LOGGER.info("Subscribe to '{}'", channel);
        final EventSubscription s = new EventSubscription(channel, callback);

        final List<Subscription> subscriptions = subscriptionsMap.getOrDefault(channel, new ArrayList<>());
        subscriptions.add(s);
        subscriptionsMap.put(channel, subscriptions);

        return s;
    }

    /**
     * Unsubscribe from a netty channel
     *
     * @param subscription The subscription to revoke.
     */
    @Override
    public void revoke(final NettyEventBusClient.Subscription subscription) {
        LOGGER.info("Unsubscribe from '{}'", subscription.getChannel());
        final List<Subscription> subscriptions = subscriptionsMap.getOrDefault(subscription.getChannel(), new ArrayList<>());
        final String channel = subscription.getChannel();
        subscriptions.remove(subscription);
        subscriptionsMap.put(channel, subscriptions);
    }

    /**
     * Publish a new message through netty in a channel
     *
     * @param message The message to publish.
     */
    @Override
    public void publish(final NettyEventBusClient.Message message) {
        if (nettyChannel == null)
            return;

        try {
            final String msg = new ObjectMapper().writeValueAsString(message);
            nettyChannel.writeAndFlush(msg + "\r\n");
            LOGGER.info("Sent message on '{}'", message.getChannel());
        } catch (Exception e) {
            LOGGER.error("Impossible to publish: {}", e.getMessage());
        }
    }

    private void trigger(final NettyEventBusClient.Message message) {
        LOGGER.info("On '{}': receive: '{}'", message.getChannel(), message.getContent());
        subscriptionsMap.getOrDefault(message.getChannel(), new ArrayList<>())
                .forEach(c -> c.getCallback().accept(message));
    }

}
