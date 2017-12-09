package com.epita.domain;

import java.util.List;

@SuppressWarnings("unused")
public class Term {
    public String token;
    public List<Integer> position;
    public double frequency;

    public Term() {
    }

    public Term(final String token, final List<Integer> position, final double frequency) {
        this.token = token;
        this.position = position;
        this.frequency = frequency;
    }
}
