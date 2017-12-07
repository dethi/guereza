package com.epita.guereza.winter;

public class Singleton<BEAN_TYPE> implements Provider<BEAN_TYPE> {
    private final BEAN_TYPE instance;

    public Singleton(final BEAN_TYPE instance) {
        this.instance = instance;
    }

    @Override
    public BEAN_TYPE getInstance(final Scope scope) {
        return instance;
    }
}
