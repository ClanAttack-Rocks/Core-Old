package rocks.clanattack.impl.util.annotation

import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import rocks.clanattack.entry.find
import rocks.clanattack.entry.plugin.Loader
import kotlin.reflect.KClass

object AnnotationScanner {

    fun findClasses(annotation: KClass<out Annotation>, classLoader: ClassLoader, basePackage: String) =
        Reflections(
            ConfigurationBuilder()
                .addClassLoader(classLoader)
                .setUrls(ClasspathHelper.forPackage(basePackage, classLoader))
                .setScanners(TypeAnnotationsScanner(), SubTypesScanner())
        ).getTypesAnnotatedWith(annotation.java)
            .map { it.kotlin }

    fun findClasses(annotation: KClass<out Annotation>) =
        Reflections(
            ConfigurationBuilder()
                .addClassLoaders(find<Loader>().classLoaders.keys)
                .setUrls(find<Loader>().classLoaders.map { (classLoader, basePackage) ->
                    ClasspathHelper.forPackage(
                        basePackage,
                        classLoader
                    )
                }.flatten())
                .setScanners(TypeAnnotationsScanner(), SubTypesScanner())
        ).getTypesAnnotatedWith(annotation.java)
            .map { it.kotlin }

    fun findMethods(annotation: KClass<out Annotation>, classLoader: ClassLoader, basePackage: String) =
        Reflections(
            ConfigurationBuilder()
                .addClassLoader(classLoader)
                .setUrls(ClasspathHelper.forPackage(basePackage, classLoader))
                .setScanners(MethodAnnotationsScanner(), SubTypesScanner())
        ).getMethodsAnnotatedWith(annotation.java)
            .filterNotNull()

    fun findMethods(annotation: KClass<out Annotation>) =
        Reflections(
            ConfigurationBuilder()
                .addClassLoaders(find<Loader>().classLoaders.keys)
                .setUrls(find<Loader>().classLoaders.map { (classLoader, basePackage) ->
                    ClasspathHelper.forPackage(
                        basePackage,
                        classLoader
                    )
                }.flatten())
                .setScanners(MethodAnnotationsScanner(), SubTypesScanner())
        ).getMethodsAnnotatedWith(annotation.java)
            .filterNotNull()


}