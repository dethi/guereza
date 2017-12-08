package com.epita.guereza.eventbus;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.function.Consumer;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    private final Consumer<NettyMessage> consumer;
    public NettyClientInitializer(Consumer<NettyMessage> c) {
        this.consumer = c;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast( new StringDecoder());
        pipeline.addLast( new StringEncoder());

        pipeline.addLast(new NettyClientHandler(this.consumer));
    }
}
