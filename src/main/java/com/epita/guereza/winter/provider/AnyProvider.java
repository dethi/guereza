package com.epita.guereza.winter.provider;

import com.epita.guereza.winter.Aspect;
import com.epita.guereza.winter.Scope;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AnyProvider<BEAN_TYPE> implements Provider<BEAN_TYPE> {
    private final Map<Method, List<Consumer<Scope>>> beforeConsumers = new HashMap<>();
    private final Map<Method, List<Consumer<Scope>>> afterConsumers = new HashMap<>();

    Class<BEAN_TYPE> klass;

    protected abstract BEAN_TYPE createInstance(final Scope scope);

    private Aspect getAspect(final Scope scope, final Object target) {
        return new Aspect(beforeConsumers, afterConsumers, scope, target);
    }

    public BEAN_TYPE getInstance(final Class<BEAN_TYPE> klass, final Scope scope) {
        BEAN_TYPE target = createInstance(scope);
        if (beforeConsumers.size() == 0 && afterConsumers.size() == 0) {
            return target;
        }

        Object proxy = Proxy.newProxyInstance(
                klass.getClassLoader(), new Class<?>[]{klass}, getAspect(scope, target));
        return klass.cast(proxy);
    }

    public Provider<BEAN_TYPE> before(final Method method, final Consumer<Scope> consumer) {
        register(beforeConsumers, method, consumer);
        return this;
    }

    public Provider<BEAN_TYPE> after(final Method method, final Consumer<Scope> consumer) {
        register(afterConsumers, method, consumer);
        return this;
    }

    private void register(Map<Method, List<Consumer<Scope>>> consumers,
                          final Method method,
                          final Consumer<Scope> consumer) {

        if (!klass.isInterface()) {
            throw new InterfaceRequiredException();
        }

        final List<Consumer<Scope>> listConsumer = consumers.getOrDefault(method, new ArrayList<>());
        listConsumer.add(consumer);
        consumers.put(method, listConsumer);
    }
}
