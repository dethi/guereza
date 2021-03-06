package com.epita.winter;

import com.epita.winter.provider.Provider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class Scope {
    private final Map<Class<?>, Provider<?>> providers = new HashMap<>();
    private final Map<Object, Class<?>> instances = new HashMap<>();

    private final Scope parent;
    private boolean isBlock = false;

    public Scope() {
        this.parent = null;
    }

    private Scope(final Scope parent) {
        this.parent = parent;
    }

    public static <BEAN_TYPE> Method getMethod(final Class<BEAN_TYPE> klass,
                                               final String name,
                                               final Class<?>... parameterTypes) {
        try {
            return klass.getMethod(name, parameterTypes);
        } catch (final NoSuchMethodException e) {
            throw new NoSuchElementException();
        }
    }

    public Scope scope() {
        return new Scope(this);
    }

    @SuppressWarnings("unchecked")
    public <BEAN_TYPE> BEAN_TYPE instanceOf(final Class<BEAN_TYPE> klass) {
        if (parent == null && !providers.containsKey(klass)) {
            throw new NoSuchElementException();
        }
        if (parent != null && !providers.containsKey(klass)) {
            return parent.instanceOf(klass);
        }

        final Provider<BEAN_TYPE> provider = (Provider<BEAN_TYPE>) providers.get(klass);
        final BEAN_TYPE instance = provider.getInstance(klass, this);
        if (isBlock) {
            instances.put(instance, klass);
        }
        return instance;
    }

    public <BEAN_TYPE> Scope register(final Provider<BEAN_TYPE> provider) {
        providers.put(provider.getInstanceClass(), provider);
        return this;
    }

    public <BEAN_TYPE> Scope unregister(final Class<BEAN_TYPE> klass) {
        providers.remove(klass);
        return this;
    }

    @SuppressWarnings("unchecked")
    private <BEAN_TYPE> void release(final Class<? extends BEAN_TYPE> klass, final BEAN_TYPE target) {
        if (providers.containsKey(klass)) {
            final Provider<BEAN_TYPE> provider = (Provider<BEAN_TYPE>) providers.get(klass);
            provider.callAfterDestroy(this, target);
        }
    }

    public void block(final Consumer<Scope> consumer) {
        isBlock = true;
        consumer.accept(this);

        for (final Map.Entry<Object, Class<?>> entry : instances.entrySet()) {
            release(entry.getValue(), entry.getKey());
        }
        instances.clear();
        isBlock = false;
    }
}
