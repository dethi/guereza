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

    private Aspect getAspect(final Scope scope, final Object target) {
        return new Aspect(beforeConsumers, afterConsumers, scope, target);
    }

    public Object getInstanceOrProxy(final Class<?> klass, final Scope scope) {
        Object target = getInstance(scope);
        if (beforeConsumers.size() == 0 && afterConsumers.size() == 0) {
            return target;
        }

        return Proxy.newProxyInstance(klass.getClassLoader(), new Class<?>[]{klass}, getAspect(scope, target));
    }

    public Provider<BEAN_TYPE> before(final Method method, final Consumer<Scope> consumer) {
        final List<Consumer<Scope>> consumers = beforeConsumers.getOrDefault(method, new ArrayList<>());
        consumers.add(consumer);
        beforeConsumers.put(method, consumers);
        return this;
    }

    public Provider<BEAN_TYPE> after(final Method method, final Consumer<Scope> consumer) {
        final List<Consumer<Scope>> consumers = afterConsumers.getOrDefault(method, new ArrayList<>());
        consumers.add(consumer);
        afterConsumers.put(method, consumers);
        return this;
    }
}
