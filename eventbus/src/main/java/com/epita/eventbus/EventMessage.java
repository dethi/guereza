package com.epita.eventbus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventMessage implements EventBusClient.Message {

    private String channel;
    private String messageType;
    private String content;

    @SuppressWarnings("unused")
    public EventMessage() {
    }

    public EventMessage(final String channel, final Object content) throws JsonProcessingException {
        this.channel = channel;
        this.messageType = content.getClass().getName();
        this.content = new ObjectMapper().writeValueAsString(content);
    }

    @Override
    public String getChannel() {
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
