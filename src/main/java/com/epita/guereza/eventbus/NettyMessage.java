package com.epita.guereza.eventbus;

public class NettyMessage implements EventBusClient.Message {
    private final EventBusClient.Channel channel;
    private final String messageType;
    private final String content;


    public NettyMessage(final EventBusClient.Channel channel, final String messageType, final String content) {
        this.channel = channel;
        this.messageType = messageType;
        this.content = content;
    }

    @Override
    public EventBusClient.Channel getChannel() {
        return channel;
    }

    @Override
    public String getMessageType() {
        return messageType;
    }

    @Override
    public String getContent() {
        return content;
    }
}
