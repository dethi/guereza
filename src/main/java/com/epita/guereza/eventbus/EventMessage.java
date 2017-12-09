package com.epita.guereza.eventbus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventMessage implements EventBusClient.Message {

    private EventBusClient.Channel channel;
    private String messageType;
    private String content;

    public EventMessage() {}

    public EventMessage(final EventBusClient.Channel channel, final Object content) throws JsonProcessingException {
        this.channel = channel;
        this.messageType = content.getClass().getName();
        this.content = new ObjectMapper().writeValueAsString(content);
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
