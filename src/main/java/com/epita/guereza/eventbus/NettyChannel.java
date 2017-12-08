package com.epita.guereza.eventbus;

public class NettyChannel implements EventBusClient.Channel {
    private final String address;

    public NettyChannel(String address) {
        this.address = address;
    }

    @Override
    public String getAddress() {
        return address;
    }
}
