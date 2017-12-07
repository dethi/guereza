package com.epita.guereza.domain;

public class Term {
    public final String token;
    public final Integer[] position;
    public final double frequency;

    public Term(final String token, final Integer[] position, final double frequency) {
        this.token = token;
        this.position = position;
        this.frequency = frequency;
    }
}
