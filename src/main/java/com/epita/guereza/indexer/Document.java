package com.epita.guereza.indexer;

import java.util.HashMap;

public class Document {
    private final String url;
    private final HashMap<String, Term> terms;

    public Document(final String url, final HashMap<String, Term> terms) {
        this.url = url;
        this.terms = terms;
    }

    public String getUrl() {
        return url;
    }

    public HashMap<String, Term> getTerms() {
        return terms;
    }
}
