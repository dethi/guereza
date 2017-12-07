package com.epita.guereza.domain;

import org.jsoup.nodes.Document;

public class RawDocument {
    public final Document doc;

    public RawDocument(Document doc) {
        this.doc = doc;
    }
}
