package com.epita.guereza.winter.provider;

import com.epita.guereza.winter.Scope;

import java.util.function.Function;

public class Prototype<BEAN_TYPE> extends AnyProvider<BEAN_TYPE> {
    private final Function<Scope, BEAN_TYPE> initiator;

    public Prototype(final Class<BEAN_TYPE> klass, final Function<Scope, BEAN_TYPE> initiator) {
        this.klass = klass;
        this.initiator = initiator;
    }

    @Override
    protected BEAN_TYPE createInstance(final Scope scope) {
        BEAN_TYPE target = initiator.apply(scope);
        callAfterCreate(scope, target);
        return target;
    }
}
