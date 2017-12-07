package com.epita.guereza.winter;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Scope {
    private Map<Class, Provider> providers = new HashMap<>();

    public <BEAN_TYPE> BEAN_TYPE instanceOf(Class<BEAN_TYPE> klass) {
        if (!providers.containsKey(klass)) {
            throw new NoSuchElementException();
        }

        return klass.cast(providers.get(klass).getInstance());
    }

    public <BEAN_TYPE> void bean(Class<BEAN_TYPE> klass, BEAN_TYPE instance) {
        provider(klass, new Singleton<>(instance));
    }

    public <BEAN_TYPE> void provider(Class<BEAN_TYPE> klass, Provider provider) {
        providers.put(klass, provider);
    }
}
