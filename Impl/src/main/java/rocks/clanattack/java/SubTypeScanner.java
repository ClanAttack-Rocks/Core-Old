package rocks.clanattack.java;

import io.github.classgraph.ClassGraph;
import rocks.clanattack.entry.plugin.Loader;
import rocks.clanattack.impl.JavaRegistry;

import java.util.List;

public class SubTypeScanner {

    public static List<Class<?>> getSubTypesOf(Class<?> clazz) {
        var glassGraph = new ClassGraph().enableClassInfo();

        for (var classLoader : JavaRegistry.find(Loader.class).getClassLoaders().keySet())
            glassGraph.addClassLoader(classLoader);

        try (var result = glassGraph.scan()) {
            return result.getSubclasses(clazz)
                    .loadClasses()
                    .stream()
                    .toList();
        }
    }
}
