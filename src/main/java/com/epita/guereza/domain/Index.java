package com.epita.guereza.domain;

import java.util.ArrayList;
import java.util.List;

public class Index {
    private List<Document> docs;

    public Index() {
        docs = new ArrayList<>();
    }

    public List<Document> getDocs() {
        return docs;
    }
}
