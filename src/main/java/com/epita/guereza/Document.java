package com.epita.guereza;

public class Document {
    private final String url;
    private final Term[] terms;

    public Document(final String url, final Term[] terms) {
        this.url = url;
        this.terms = terms;
    }

    public String getUrl() {
        return url;
    }

    public Term[] getTerms() {
        return terms;
    }
}
