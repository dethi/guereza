package com.epita.guereza.winter.provider;

import com.epita.guereza.winter.Aspect;
import com.epita.guereza.winter.Scope;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AnyProvider<BEAN_TYPE> implements Provider<BEAN_TYPE> {
    private final Map<Method, List<Consumer<Scope>>> beforeConsumers = new HashMap<>();
    private final Map<Method, List<Consumer<Scope>>> afterConsumers = new HashMap<>();

    public Aspect getAspect(final Scope scope, final Object target) {
        return new Aspect(beforeConsumers, afterConsumers, scope, target);
    }

    public void before(final Method method, final Consumer<Scope> consumer) {
        final List<Consumer<Scope>> consumers = beforeConsumers.getOrDefault(method, new ArrayList<>());
        consumers.add(consumer);
        beforeConsumers.put(method, consumers);
    }

    public void after(final Method method, final Consumer<Scope> consumer) {
        final List<Consumer<Scope>> consumers = afterConsumers.getOrDefault(method, new ArrayList<>());
        consumers.add(consumer);
        afterConsumers.put(method, consumers);
    }
}
