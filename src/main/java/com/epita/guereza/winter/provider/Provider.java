package com.epita.guereza.winter.provider;

import com.epita.guereza.winter.Scope;
import com.epita.guereza.winter.aspect.AspectContext;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Provider<BEAN_TYPE> {
    BEAN_TYPE getInstance(final Class<BEAN_TYPE> klass, final Scope scope);

    Provider<BEAN_TYPE> afterCreate(final BiConsumer<Scope, BEAN_TYPE> consumer);

    Provider<BEAN_TYPE> before(final Method method, final BiConsumer<Scope, BEAN_TYPE> consumer);

    Provider<BEAN_TYPE> around(final Method method, final Function<AspectContext, Object> consumer);

    Provider<BEAN_TYPE> after(final Method method, final BiConsumer<Scope, BEAN_TYPE> consumer);

    Provider<BEAN_TYPE> beforeDestroy(final Consumer<Scope> consumer);

    void unregister(final Scope scope);
}
