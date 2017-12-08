package com.epita.guereza.winter.provider;

import com.epita.guereza.winter.Scope;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public interface Provider<BEAN_TYPE> {
    BEAN_TYPE getInstance(final Class<BEAN_TYPE> klass, final Scope scope);

    Provider<BEAN_TYPE> before(final Method method, final Consumer<Scope> consumer);

    Provider<BEAN_TYPE> after(final Method method, final Consumer<Scope> consumer);
}
