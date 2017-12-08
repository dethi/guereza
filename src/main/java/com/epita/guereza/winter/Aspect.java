package com.epita.guereza.winter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Aspect implements InvocationHandler {
    private final Map<Method, List<Consumer<Scope>>> beforeConsumers;
    private final Map<Method, List<Consumer<Scope>>> afterConsumers;

    private final Scope scope;
    private final Object target;

    public Aspect(final Map<Method, List<Consumer<Scope>>> beforeConsumers,
                  final Map<Method, List<Consumer<Scope>>> afterConsumers,
                  final Scope scope,
                  final Object target) {

        this.beforeConsumers = beforeConsumers;
        this.afterConsumers = afterConsumers;
        this.scope = scope;
        this.target = target;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (beforeConsumers.containsKey(method)) {
            for (Consumer<Scope> consumer : beforeConsumers.get(method)) {
                consumer.accept(scope);
            }
        }

        Object res = method.invoke(target, args);

        if (afterConsumers.containsKey(method)) {
            for (Consumer<Scope> consumer : afterConsumers.get(method)) {
                consumer.accept(scope);
            }
        }

        return res;
    }
}
