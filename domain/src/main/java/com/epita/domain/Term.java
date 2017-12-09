package com.epita.domain;

import java.util.List;

public class Term {
    public final String token;
    public final List<Integer> position;
    public final double frequency;

    public Term(final String token, final List<Integer> position, final double frequency) {
        this.token = token;
        this.position = position;
        this.frequency = frequency;
    }
}
