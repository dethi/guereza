package com.epita.eventsourcing;

public interface Reducer {
    void reduce(Event<?> event);
}
