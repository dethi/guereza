package com.epita.guereza.indexer;

public class Term {
    private final String token;
    private final Integer[] position;
    private final double frequency;

    public Term(final String token, final Integer[] position, final double frequency) {
        this.token = token;
        this.position = position;
        this.frequency = frequency;
    }

    public String getToken() {
        return token;
    }

    public Integer[] getPosition() {
        return position;
    }

    public double getFrequency() {
        return frequency;
    }
}
