package com.epita.domain;

import java.util.HashMap;

public class Document {
    public final String url;
    public final HashMap<String, Term> terms;

    public Document(final String url, final HashMap<String, Term> terms) {
        this.url = url;
        this.terms = terms;
    }
}
