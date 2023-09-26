package rocks.clanattack.java;

import kotlin.Unit;
import rocks.clanattack.impl.util.JavaRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class MethodHelper {

    public static String getFullName(Method method) {
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }

    public static <T> T call(Method method, Class<T> resultType, Object... args) throws InvocationTargetException, IllegalAccessException {
        if (method.getParameterCount() > args.length)
            throw new IllegalArgumentException("Function " + getFullName(method) + " can have at most " + args.length + " parameters.");

        var parameters = new ArrayList<>();
        for (var i = 0; i < args.length; i++) {
            var parameter = method.getParameters()[i];
            if (parameter.getType().isInstance(args[i])) parameters.add(args[i]);
            else
                throw new IllegalArgumentException("Parameter " + parameter.getName() + " of function " + getFullName(method) + " must be of type " + parameter.getType().getName() + ".");
        }

        method.setAccessible(true);
        Object result;

        if (Modifier.isStatic(method.getModifiers())) result = method.invoke(null, parameters.toArray());
        else {
            var instance = JavaRegistry.getOrCreate(method.getDeclaringClass());
            result = method.invoke(instance, parameters.toArray());
        }

        if (result == null && resultType != Void.class && resultType != Unit.class)
            throw new IllegalStateException("Function " + getFullName(method) + " returned null, but it should return " + resultType.getName() + ".");

        if (result != null && !resultType.isInstance(result))
            throw new IllegalStateException("Function " + getFullName(method) + " returned " + result.getClass().getName() + ", but it should return " + resultType.getName() + ".");

        return resultType.cast(result);
    }

    public static void call(Method method, Object... args) throws InvocationTargetException, IllegalAccessException {
        call(method, Void.class, args);
    }

}
