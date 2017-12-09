package com.epita.winter.provider;

import com.epita.winter.Scope;

import java.util.function.Function;

public class LazySingleton<BEAN_TYPE> extends AnyProvider<BEAN_TYPE> {
    private final Function<Scope, BEAN_TYPE> initiator;
    private BEAN_TYPE instance;

    public LazySingleton(final Class<BEAN_TYPE> klass, final Function<Scope, BEAN_TYPE> initiator) {
        this.klass = klass;
        this.initiator = initiator;
    }

    @Override
    protected BEAN_TYPE createInstance(final Scope scope) {
        if (instance == null) {
            instance = initiator.apply(scope);
            callAfterCreate(scope, instance);
        }
        return instance;
    }
}