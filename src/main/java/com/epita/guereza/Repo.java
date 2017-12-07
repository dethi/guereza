package com.epita.guereza;

public interface Repo {
    void store(String[] urls);

    String nextUrl();
}
