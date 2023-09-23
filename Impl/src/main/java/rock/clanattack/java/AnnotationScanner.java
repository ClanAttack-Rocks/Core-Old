package rock.clanattack.java;

import io.github.classgraph.ClassGraph;
import rocks.clanattack.entry.plugin.Loader;
import rocks.clanattack.impl.util.JavaMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AnnotationScanner {

    public static List<Class<?>> getAnnotatedClasses(Class<? extends Annotation> clazz, ClassLoader loader, String basePackage) {
        try (var result = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .addClassLoader(loader)
                .acceptPackages(basePackage)
                .scan()) {
            return result.getClassesWithAnnotation(clazz)
                    .loadClasses();
        }
    }

    public static List<Class<?>> getAnnotatedClasses(Class<? extends Annotation> clazz) {
        var loaders = JavaMap.find(Loader.class).getClassLoaders().entrySet();
        var glassGraph = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(loaders.stream().map(Map.Entry::getValue).toArray(String[]::new));

        for (var classLoader : loaders) glassGraph.addClassLoader(classLoader.getKey());


        try (var result = glassGraph.scan()) {
            return result.getClassesWithAnnotation(clazz).loadClasses();
        }
    }

    public static List<Method> getAnnotatedMethods(Class<? extends Annotation> clazz, ClassLoader loader, String basePackage) {
        try (var result = new ClassGraph()
                .enableClassInfo()
                .enableMethodInfo()
                .enableAnnotationInfo()
                .addClassLoader(loader)
                .acceptPackages(basePackage)
                .scan()) {

            return result.getClassesWithMethodAnnotation(clazz)
                    .loadClasses()
                    .stream()
                    .flatMap(c -> Arrays.stream(c.getMethods()))
                    .filter(m -> m.isAnnotationPresent(clazz))
                    .toList();

        }
    }

    public static List<Method> getAnnotatedMethods(Class<? extends Annotation> clazz) {
        var loaders = JavaMap.find(Loader.class).getClassLoaders().entrySet();
        var glassGraph = new ClassGraph()
                .enableClassInfo()
                .enableMethodInfo()
                .enableAnnotationInfo()
                .acceptPackages(loaders.stream().map(Map.Entry::getValue).toArray(String[]::new));

        for (var classLoader : loaders) glassGraph.addClassLoader(classLoader.getKey());

        try (var result = glassGraph.scan()) {
            return result.getClassesWithMethodAnnotation(clazz)
                    .loadClasses()
                    .stream()
                    .flatMap(c -> Arrays.stream(c.getMethods()))
                    .filter(m -> m.isAnnotationPresent(clazz))
                    .toList();
        }
    }

}
