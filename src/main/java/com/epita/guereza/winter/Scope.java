package com.epita.guereza.winter;

import com.epita.guereza.winter.provider.Provider;
import com.epita.guereza.winter.provider.Singleton;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Scope {
    private final Map<Class<?>, Provider<?>> providers = new HashMap<>();

    public static <BEAN_TYPE> Method getMethod(Class<BEAN_TYPE> klass, String name, Class<?>... parameterTypes) {
        try {
            return klass.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new NoSuchElementException();
        }
    }

    public <BEAN_TYPE> BEAN_TYPE instanceOf(final Class<BEAN_TYPE> klass) {
        if (!providers.containsKey(klass)) {
            throw new NoSuchElementException();
        }

        Object proxy = providers.get(klass).getInstanceOrProxy(klass, this);
        return klass.cast(proxy);
    }

    public <BEAN_TYPE> Provider<BEAN_TYPE> bean(final Class<BEAN_TYPE> klass, final BEAN_TYPE instance) {
        return provide(klass, new Singleton<>(instance));
    }

    public <BEAN_TYPE> Provider<BEAN_TYPE> provide(final Class<BEAN_TYPE> klass, final Provider<BEAN_TYPE> provider) {
        providers.put(klass, provider);
        return provider;
    }
}
