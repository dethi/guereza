package com.epita.guereza.eventsourcing;

public interface Reducer {
    void reduce(Event<?> event);
}
