package com.epita.guereza.winter;

public interface Provider<BEAN_TYPE> {
    BEAN_TYPE getInstance(final Scope scope);
}
