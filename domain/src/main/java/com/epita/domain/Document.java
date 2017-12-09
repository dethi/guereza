package com.epita.domain;

import java.util.HashMap;

public class Document {
    public String url;
    public HashMap<String, Term> terms;

    public Document() {}

    public Document(final String url, final HashMap<String, Term> terms) {
        this.url = url;
        this.terms = terms;
    }
}
