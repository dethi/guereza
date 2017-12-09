package com.epita.eventbus.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);
    private static final ChannelGroup channels = new DefaultChannelGroup(new DefaultEventExecutor());

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        final Channel incoming = ctx.channel();
        LOGGER.info("Handling connection");
        channels.add(incoming);
    }

    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) {
        final Channel incoming = ctx.channel();
        channels.remove(incoming);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final String s) {
        final Channel incoming = channelHandlerContext.channel();
        LOGGER.info("Reading: {}", s);
        for (final Channel channel : channels) {
            if (incoming != channel)
                channel.writeAndFlush(s + "\n");
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
