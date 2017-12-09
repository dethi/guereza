package com.epita.guereza.eventbus;

public class EventChannel implements EventBusClient.Channel {

    private String address;

    public EventChannel() { }

    public EventChannel(String address) {
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
