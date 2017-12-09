package com.epita.winter.provider;

import com.epita.winter.Scope;
import com.epita.winter.aspect.AspectContext;
import com.epita.winter.aspect.AspectInvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AnyProvider<BEAN_TYPE> implements Provider<BEAN_TYPE> {
    private final Map<Method, List<BiConsumer<Scope, BEAN_TYPE>>> beforeConsumers = new LinkedHashMap<>();
    private final Map<Method, List<BiConsumer<Scope, BEAN_TYPE>>> afterConsumers = new LinkedHashMap<>();
    private final Map<Method, List<Function<AspectContext, Object>>> aroundFunctions = new LinkedHashMap<>();
    private final List<BiConsumer<Scope, BEAN_TYPE>> afterCreateConsumers = new ArrayList<>();
    private final List<BiConsumer<Scope, BEAN_TYPE>> beforeDestroyConsumers = new ArrayList<>();

    Class<BEAN_TYPE> klass;

    public Class<BEAN_TYPE> getInstanceClass() {
        return klass;
    }

    public BEAN_TYPE getInstance(final Class<BEAN_TYPE> klass, final Scope scope) {
        BEAN_TYPE target = createInstance(scope);
        if (beforeConsumers.size() == 0 && afterConsumers.size() == 0) {
            return target;
        }

        Object proxy = Proxy.newProxyInstance(
                klass.getClassLoader(), new Class<?>[]{klass}, getAspectInvocationHandler(scope, target));
        return klass.cast(proxy);
    }

    public Provider<BEAN_TYPE> afterCreate(final BiConsumer<Scope, BEAN_TYPE> consumer) {
        afterCreateConsumers.add(consumer);
        return this;
    }

    public Provider<BEAN_TYPE> before(final Method method, final BiConsumer<Scope, BEAN_TYPE> consumer) {
        register(beforeConsumers, method, consumer);
        return this;
    }

    public Provider<BEAN_TYPE> around(final Method method, final Function<AspectContext, Object> consumer) {
        if (!klass.isInterface()) {
            throw new InterfaceRequiredException();
        }

        final List<Function<AspectContext, Object>> listConsumer =
                aroundFunctions.getOrDefault(method, new ArrayList<>());
        listConsumer.add(consumer);
        aroundFunctions.put(method, listConsumer);

        return this;
    }

    public Provider<BEAN_TYPE> after(final Method method, final BiConsumer<Scope, BEAN_TYPE> consumer) {
        register(afterConsumers, method, consumer);
        return this;
    }

    public Provider<BEAN_TYPE> beforeDestroy(final BiConsumer<Scope, BEAN_TYPE> consumer) {
        beforeDestroyConsumers.add(consumer);
        return this;
    }

    public void callAfterDestroy(final Scope scope, final BEAN_TYPE target) {
        for (BiConsumer<Scope, BEAN_TYPE> consumer : beforeDestroyConsumers) {
            consumer.accept(scope, target);
        }
    }

    protected abstract BEAN_TYPE createInstance(final Scope scope);

    private AspectInvocationHandler getAspectInvocationHandler(final Scope scope, final BEAN_TYPE target) {
        return new AspectInvocationHandler<>(beforeConsumers, afterConsumers, aroundFunctions, scope, target);
    }

    void callAfterCreate(final Scope scope, final BEAN_TYPE target) {
        for (BiConsumer<Scope, BEAN_TYPE> consumer : afterCreateConsumers) {
            consumer.accept(scope, target);
        }
    }

    private void register(final Map<Method, List<BiConsumer<Scope, BEAN_TYPE>>> consumers,
                          final Method method,
                          final BiConsumer<Scope, BEAN_TYPE> consumer) {

        if (!klass.isInterface()) {
            throw new InterfaceRequiredException();
        }

        final List<BiConsumer<Scope, BEAN_TYPE>> listConsumer = consumers.getOrDefault(method, new ArrayList<>());
        listConsumer.add(consumer);
        consumers.put(method, listConsumer);
    }
}
