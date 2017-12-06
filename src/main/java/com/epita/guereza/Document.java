package com.epita.guereza;

import java.util.List;

public class Document {
    private final String url;
    private final List<Term> terms;

    public Document(final String url, final List<Term> terms) {
        this.url = url;
        this.terms = terms;
    }

    public String getUrl() {
        return url;
    }

    public List<Term> getTerms() {
        return terms;
    }
}
