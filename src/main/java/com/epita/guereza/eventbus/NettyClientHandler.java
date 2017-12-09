package com.epita.guereza.eventbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.function.Consumer;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {
    private final Consumer<EventMessage> consumer;

    public NettyClientHandler(Consumer<EventMessage> c) {
        this.consumer = c;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.err.println(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        EventMessage res = new ObjectMapper().readValue(msg.toString(), EventMessage.class);
        consumer.accept(res);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
