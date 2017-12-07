package com.epita.guereza;

import java.util.HashMap;
import java.util.List;

public interface IIndexer {

    Document index(final String url);
    HashMap<Document, Double> search(final List<Document> docs, final String query);
    void publish(final Index index, final Document doc);
}
