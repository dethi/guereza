package com.epita.guereza.eventbus;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
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
    private final Map<EventBusClient.Channel, List<Subscription>> subscriptionsMap;
    private io.netty.channel.Channel nettyChannel;
    private final EventLoopGroup group;

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

        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChatClientInitializer());

        try {
            nettyChannel = bootstrap.connect(host, port).sync().channel();
        } catch (Exception e) {
            LOGGER.error("EventBusClient: impossible to connect to {}:{}", host, port);
            //e.printStackTrace();
            return false;
        }

        LOGGER.info("EventBusClient: listening on {}:{}", host, port);
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
     * @return
     */
    @Override
    public Subscription subscribe(final NettyEventBusClient.Channel channel, final Consumer<Message> callback) {
        LOGGER.info("EventBusClient: subscribe to {}", channel.getAddress());
        NettySubscription s = new NettySubscription(channel, callback);

        final List<Subscription> subscriptions = subscriptionsMap.getOrDefault(channel, new ArrayList<>());
        subscriptions.add(s);
        subscriptionsMap.put(channel, subscriptions);

        return s;
    }

    private void trigger(final NettyEventBusClient.Channel channel, final NettyEventBusClient.Message message) {
        LOGGER.info("EventBusClient: receive on {}", channel.getAddress());
        subscriptionsMap.getOrDefault(channel, new ArrayList<>())
                .forEach(c -> c.getCallback().accept(message));
    }

    /**
     * Unsubscribe from a netty channel
     *
     * @param subscription The subscription to revoke.
     */
    @Override
    public void revoke(final NettyEventBusClient.Subscription subscription) {
        LOGGER.info("EventBusClient: unsubscribe from {}", subscription.getChannel().getAddress());
        final List<Subscription> subscriptions = subscriptionsMap.getOrDefault(subscription.getChannel(), new ArrayList<>());
        subscriptions.remove(subscription.getCallback());
        subscriptionsMap.put(subscription.getChannel(), subscriptions);
    }

    /**
     * Publish a new message through netty in a channel
     *
     * @param channel The channel to publish to.
     * @param message The message to publish.
     */
    @Override
    public void publish(final NettyEventBusClient.Channel channel, final NettyEventBusClient.Message message) {
        if (nettyChannel == null)
            return;

        ChannelFuture cf = nettyChannel.write(message);
        nettyChannel.flush();
        if (!cf.isSuccess()) {
            System.out.println("Send failed: " + cf.cause());
        }
        LOGGER.info("EventBusClient: sent message on {}", channel.getAddress());
    }

}
