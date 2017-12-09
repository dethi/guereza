package com.epita.guereza.eventbus;

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
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        final Channel incoming = ctx.channel();
        LOGGER.info("Server: Handling connection");
        channels.add(incoming);
    }

    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        final Channel incoming = ctx.channel();
        channels.remove(incoming);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final String s) throws Exception {
        final Channel incoming = channelHandlerContext.channel();
        LOGGER.info("Server reading: {}", s);
        for (final Channel channel : channels) {
            channel.writeAndFlush(s + "\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
