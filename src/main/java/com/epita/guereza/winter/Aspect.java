package com.epita.guereza.winter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Aspect<BEAN_TYPE> implements InvocationHandler {
    private final Map<Method, List<BiConsumer<Scope, BEAN_TYPE>>> beforeConsumers;
    private final Map<Method, List<BiConsumer<Scope, BEAN_TYPE>>> afterConsumers;
    private final Map<Method, List<Function<AspectContext, Object>>> aroundFunctions;

    private final Scope scope;
    private final BEAN_TYPE target;

    public Aspect(final Map<Method, List<BiConsumer<Scope, BEAN_TYPE>>> beforeConsumers,
                  final Map<Method, List<BiConsumer<Scope, BEAN_TYPE>>> afterConsumers,
                  final Map<Method, List<Function<AspectContext, Object>>> aroundFunctions,
                  final Scope scope,
                  final BEAN_TYPE target) {

        this.beforeConsumers = beforeConsumers;
        this.afterConsumers = afterConsumers;
        this.aroundFunctions = aroundFunctions;
        this.scope = scope;
        this.target = target;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (beforeConsumers.containsKey(method)) {
            for (BiConsumer<Scope, BEAN_TYPE> consumer : beforeConsumers.get(method)) {
                consumer.accept(scope, target);
            }
        }

        Object res;
        if (aroundFunctions.containsKey(method)) {
            AspectContext<BEAN_TYPE> context =
                    new AspectContext<>(target, method, args, aroundFunctions.get(method));

            res = context.invoke();
        } else {
            res = method.invoke(target, args);
        }

        if (afterConsumers.containsKey(method)) {
            for (BiConsumer<Scope, BEAN_TYPE> consumer : afterConsumers.get(method)) {
                consumer.accept(scope, target);
            }
        }

        return res;
    }
}
