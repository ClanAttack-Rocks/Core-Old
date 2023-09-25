package rock.clanattack.java;

import rocks.clanattack.entry.Registry;
import rocks.clanattack.impl.JavaRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

public class ClassHelper {

    public static <T> T createInstance(Class<T> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            var instanceFiled = clazz.getDeclaredField("INSTANCE");
            if (instanceFiled.getType() == clazz
                    && Modifier.isStatic(instanceFiled.getModifiers())
                    && Modifier.isPublic(instanceFiled.getModifiers())
                    && Modifier.isFinal(instanceFiled.getModifiers()))
                return clazz.cast(instanceFiled.get(null));
        } catch (Exception ignored) {
        }

        var result = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0 || (c.getParameterCount() == 1 && c.getParameterTypes()[0] == Registry.class))
                .max(Comparator.comparingInt(Constructor::getParameterCount));

        if (result.isPresent()) {
            var constructor = result.get();
            constructor.setAccessible(true);

            if (constructor.getParameterCount() == 0) return clazz.cast(constructor.newInstance());
            else return clazz.cast(constructor.newInstance(JavaRegistry.find(Registry.class)));
        }

        throw new IllegalArgumentException("The class " + clazz.getName() + " does not have a eligible constructor for automatic creation.");
    }

}
