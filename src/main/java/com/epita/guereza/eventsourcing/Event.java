package com.epita.guereza.eventsourcing;

@SuppressWarnings("WeakerAccess")
public class Event<BEAN_TYPE> {
    public final String type;
    public final BEAN_TYPE obj;

    public Event(final String type, final BEAN_TYPE obj) {
        this.type = type;
        this.obj = obj;
    }
}
