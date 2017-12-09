package com.epita.guereza.eventsourcing;

import java.util.ArrayList;
import java.util.List;

public class EventStore {
    private final List<Event<?>> events = new ArrayList<>();
    private final List<Reducer> reducers = new ArrayList<>();

    public void addReducer(final Reducer reducer) {
        reducers.add(reducer);
    }

    public void dispatch(final Event<?> event) {
        events.add(event);
        reduceAll(event);
    }

    private void reduceAll(final Event<?> event) {
        for (Reducer reducer : reducers) {
            reducer.reduce(event);
        }
    }
}
