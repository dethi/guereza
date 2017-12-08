package com.epita.guereza.eventbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.function.Consumer;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {
    private final Consumer<NettyMessage> consumer;

    public NettyClientHandler(Consumer<NettyMessage> c) {
        this.consumer = c;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.err.println(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage res = new ObjectMapper().readValue(msg.toString(), NettyMessage.class);
        consumer.accept(res);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
