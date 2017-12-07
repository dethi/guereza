package com.epita.guereza.winter;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Scope {
    private final Map<Class, Provider> providers = new HashMap<>();

    public <BEAN_TYPE> BEAN_TYPE instanceOf(final Class<BEAN_TYPE> klass) {
        if (!providers.containsKey(klass)) {
            throw new NoSuchElementException();
        }

        return klass.cast(providers.get(klass).getInstance(this));
    }

    public <BEAN_TYPE> void bean(final Class<BEAN_TYPE> klass, final BEAN_TYPE instance) {
        provider(klass, new Singleton<>(instance));
    }

    public <BEAN_TYPE> void provider(final Class<BEAN_TYPE> klass, final Provider provider) {
        providers.put(klass, provider);
    }
}
