package com.epita.guereza.winter;

import com.epita.guereza.winter.provider.Provider;
import com.epita.guereza.winter.provider.Singleton;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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

        Provider<?> provider = providers.get(klass);
        Object target = provider.getInstance(this);
        Object proxy = Proxy.newProxyInstance(
                klass.getClassLoader(),
                new Class<?>[]{klass},
                provider.getAspect(this, target));

        return klass.cast(proxy);
    }

    public <BEAN_TYPE> void bean(final Class<BEAN_TYPE> klass, final BEAN_TYPE instance) {
        provide(klass, new Singleton<>(instance));
    }

    public <BEAN_TYPE> void provide(final Class<BEAN_TYPE> klass, final Provider provider) {
        providers.put(klass, provider);
    }
}
