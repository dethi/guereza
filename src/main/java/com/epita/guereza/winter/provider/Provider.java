package com.epita.guereza.winter.provider;

import com.epita.guereza.winter.Scope;
import com.epita.guereza.winter.aspect.AspectContext;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface Provider<BEAN_TYPE> {

    /**
     * Return an instance created by the provider. The instance could be a proxy object if any
     * around aspect are registered.
     *
     * @param klass The class
     * @param scope The scope
     * @return A new instance
     */
    BEAN_TYPE getInstance(final Class<BEAN_TYPE> klass, final Scope scope);

    /**
     * Add an aspect that run after the creation of a new instance.
     *
     * @param consumer The aspect to run
     * @return The provider
     */
    Provider<BEAN_TYPE> afterCreate(final BiConsumer<Scope, BEAN_TYPE> consumer);

    /**
     * Add an aspect that run before each invocation of the method.
     *
     * @param method   The method
     * @param consumer The aspect to run
     * @return The provider
     */
    Provider<BEAN_TYPE> before(final Method method, final BiConsumer<Scope, BEAN_TYPE> consumer);

    /**
     * Add an aspect that can call the method or not. The aspect is in charge of invoking the method using
     * the context object. The aspect should return the result object of the method call or null.
     *
     * @param method   The method
     * @param consumer The aspect to run
     * @return The provider
     */
    Provider<BEAN_TYPE> around(final Method method, final Function<AspectContext, Object> consumer);

    /**
     * Add an aspect that run after each invocation of the method.
     *
     * @param method   The method
     * @param consumer The aspect to run
     * @return The provider
     */
    Provider<BEAN_TYPE> after(final Method method, final BiConsumer<Scope, BEAN_TYPE> consumer);

    /**
     * Add an aspect that run after the destroy
     *
     * @param consumer The aspect to run
     * @return The provider
     */
    Provider<BEAN_TYPE> beforeDestroy(final BiConsumer<Scope, BEAN_TYPE> consumer);

    /**
     * Call all registered after destroy aspect.
     *
     * @param scope  The scope
     * @param target The instance
     */
    void callAfterDestroy(final Scope scope, final BEAN_TYPE target);
}
