package com.epita.winter.aspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public class AspectContext<BEAN_TYPE> {
    public final BEAN_TYPE target;
    public final Method method;
    public final Object[] args;

    private final List<Function<AspectContext, Object>> functions;

    public AspectContext(final BEAN_TYPE target, final Method method, final Object[] args,
                         final List<Function<AspectContext, Object>> functions) {

        this.target = target;
        this.method = method;
        this.args = args;
        this.functions = functions;
    }

    Object invoke() {
        AspectContext<BEAN_TYPE> context =
                new AspectContext<>(target, method, args, functions.subList(1, functions.size()));

        return functions.get(0).apply(context);
    }

    public Object call() {
        if (functions.isEmpty()) {
            try {
                return method.invoke(target, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        }

        return invoke();
    }
}
