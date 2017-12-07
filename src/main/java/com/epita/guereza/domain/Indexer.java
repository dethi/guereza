package com.epita.guereza.domain;

import java.util.List;
import java.util.Map;

public interface Indexer {

    Document index(final String url);

    Map<Document, Double> search(final List<Document> docs, final String query);

    void publish(final Index index, final Document doc);
}
