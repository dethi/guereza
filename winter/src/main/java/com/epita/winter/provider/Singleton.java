package com.epita.winter.provider;

import com.epita.winter.Scope;

public class Singleton<BEAN_TYPE> extends AnyProvider<BEAN_TYPE> {
    private final BEAN_TYPE instance;

    public Singleton(final Class<BEAN_TYPE> klass, final BEAN_TYPE instance) {
        this.klass = klass;
        this.instance = instance;
    }

    @Override
    protected BEAN_TYPE createInstance(final Scope scope) {
        return instance;
    }
}
