package com.epita.guereza.crawler;

import org.jsoup.nodes.Document;

public class RawDocument {
    private final Document doc;

    public RawDocument(Document doc) {
        this.doc = doc;
    }

    public Document getDoc() {
        return doc;
    }
}
