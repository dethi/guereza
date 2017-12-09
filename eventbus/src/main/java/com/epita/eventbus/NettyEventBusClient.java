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
            LOGGER.error("EventBusClient: impossible to connect to {}:{}", host, port);
            //e.printStackTrace();
            return false;
        }
        LOGGER.info("EventBusClient: connected on {}:{}", host, port);

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
    public Subscription subscribe(final NettyEventBusClient.Channel channel, final Consumer<Message> callback) {
        LOGGER.info("EventBusClient: subscribe to '{}'", channel.getAddress());
        final EventSubscription s = new EventSubscription(channel, callback);

        final List<Subscription> subscriptions = subscriptionsMap.getOrDefault(channel.getAddress(), new ArrayList<>());
        subscriptions.add(s);
        subscriptionsMap.put(channel.getAddress(), subscriptions);

        return s;
    }

    /**
     * Unsubscribe from a netty channel
     *
     * @param subscription The subscription to revoke.
     */
    @Override
    public void revoke(final NettyEventBusClient.Subscription subscription) {
        LOGGER.info("EventBusClient: unsubscribe from '{}'", subscription.getChannel().getAddress());
        final List<Subscription> subscriptions = subscriptionsMap.getOrDefault(subscription.getChannel().getAddress(), new ArrayList<>());
        final Channel channel = subscription.getChannel();
        subscriptions.remove(subscription);
        subscriptionsMap.put(channel.getAddress(), subscriptions);
    }

    /**
     * Publish a new message through netty in a channel
     *
     * @param channel The channel to publish to.
     * @param message The message to publish.
     */
    @Override
    public void publish(final Channel channel, final NettyEventBusClient.Message message) {
        if (nettyChannel == null)
            return;

        try {
            final String msg = new ObjectMapper().writeValueAsString(message);
            nettyChannel.writeAndFlush(msg + "\r\n");
            LOGGER.info("EventBusClient: sent message on '{}'", message.getChannel().getAddress());
        } catch (Exception $e) {
            LOGGER.error("EventBusClient: Impossible to publish");
        }
    }

    private void trigger(final NettyEventBusClient.Message message) {
        LOGGER.info("EventBusClient: on '{}': receive: '{}'", message.getChannel().getAddress(), message.getContent());
        subscriptionsMap.getOrDefault(message.getChannel().getAddress(), new ArrayList<>())
                .forEach(c -> c.getCallback().accept(message));
    }

}
