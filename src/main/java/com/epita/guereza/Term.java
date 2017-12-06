package com.epita.guereza;

public class Term {
    private final String token;
    private final Integer[] position;
    private final Integer frequence;

    public Term(final String token, final Integer[] position, final Integer frequence) {
        this.token = token;
        this.position = position;
        this.frequence = frequence;
    }

    public String getToken() {
        return token;
    }

    public Integer[] getPosition() {
        return position;
    }

    public Integer getFrequence() {
        return frequence;
    }
}
