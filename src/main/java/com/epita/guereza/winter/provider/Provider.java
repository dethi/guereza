package com.epita.guereza.winter.provider;

import com.epita.guereza.winter.Aspect;
import com.epita.guereza.winter.Scope;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public interface Provider<BEAN_TYPE> {
    BEAN_TYPE getInstance(final Scope scope);

    Aspect getAspect(final Scope scope, final Object target);

    void before(final Method method, final Consumer<Scope> consumer);

    void after(final Method method, final Consumer<Scope> consumer);
}
