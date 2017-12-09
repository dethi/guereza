package com.epita.guereza.reducer;

import com.epita.domain.Document;
import com.epita.domain.Index;
import com.epita.eventsourcing.Event;
import com.epita.eventsourcing.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetroIndex implements Reducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetroIndex.class);

    private final Index index = new Index();

    @SuppressWarnings("unchecked")
    @Override
    public void reduce(final Event<?> event) {
        switch (event.type) {
            case "ADD_DOCUMENT":
                addDocument((Event<Document>) event);
                break;
        }
    }

    private void addDocument(final Event<Document> event) {
        index.docs.add(event.obj);
        LOGGER.info("added a document to the index");
    }
}

