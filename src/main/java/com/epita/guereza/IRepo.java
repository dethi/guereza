package com.epita.guereza;

public interface IRepo {
    void store(String[] urls);

    String nextUrl();
}
