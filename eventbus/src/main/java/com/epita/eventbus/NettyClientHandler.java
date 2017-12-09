package com.epita.eventbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.function.Consumer;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {
    private final Consumer<EventMessage> consumer;

    public NettyClientHandler(final Consumer<EventMessage> c) {
        this.consumer = c;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final String msg) {
        System.err.println(msg);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final EventMessage res = new ObjectMapper().readValue(msg.toString(), EventMessage.class);
        consumer.accept(res);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
