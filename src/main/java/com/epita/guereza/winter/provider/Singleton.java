package com.epita.guereza.winter.provider;

import com.epita.guereza.winter.Scope;

public class Singleton<BEAN_TYPE> extends AnyProvider<BEAN_TYPE> {
    private final BEAN_TYPE instance;

    public Singleton(final BEAN_TYPE instance) {
        this.instance = instance;
    }

    @Override
    public BEAN_TYPE createInstance(final Scope scope) {
        return instance;
    }
}
