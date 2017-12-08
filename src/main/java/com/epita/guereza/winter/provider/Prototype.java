package com.epita.guereza.winter.provider;

import com.epita.guereza.winter.Scope;

import java.util.function.Function;

public class Prototype<BEAN_TYPE> extends AnyProvider<BEAN_TYPE> {
    private final Function<Scope, BEAN_TYPE> initiator;

    public Prototype(Function<Scope, BEAN_TYPE> initiator) {
        this.initiator = initiator;
    }

    @Override
    public BEAN_TYPE createInstance(final Scope scope) {
        return initiator.apply(scope);
    }
}
