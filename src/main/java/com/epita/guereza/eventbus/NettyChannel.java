package com.epita.guereza.eventbus;

public class NettyChannel implements EventBusClient.Channel {

    private String address;

    public NettyChannel() { }

    public NettyChannel(String address) {
        this.address = address;
    }

    @Override
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
