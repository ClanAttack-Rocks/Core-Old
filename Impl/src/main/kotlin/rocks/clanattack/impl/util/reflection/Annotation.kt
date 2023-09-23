package rocks.clanattack.impl.util.reflection

import io.github.classgraph.ClassGraph
import rocks.clanattack.entry.find
import rocks.clanattack.entry.plugin.Loader
import kotlin.reflect.KClass
import kotlin.reflect.jvm.kotlinFunction

fun KClass<out Annotation>.getAnnotatedClasses(loader: ClassLoader, basePackage: String) =  ClassGraph()
    .enableClassInfo()
    .addClassLoader(loader)
    .acceptPackages(basePackage)
    .scan()
    .getClassesWithAnnotation(this.java)
    .map { it.loadClass() }
    .map { it.kotlin }

val KClass<out Annotation>.annotatedClasses
    get() = ClassGraph()
        .enableClassInfo()
        .also { find<Loader>().classLoaders.keys.forEach { classLoader -> it.addClassLoader(classLoader) } }
        .acceptPackages(*find<Loader>().classLoaders.values.toTypedArray())
        .scan()
        .getClassesWithAnnotation(this.java)
        .map { it.loadClass() }
        .map { it.kotlin }

fun KClass<out Annotation>.getAnnotatedMethods(loader: ClassLoader, basePackage: String) = ClassGraph()
    .enableMethodInfo()
    .addClassLoader(loader)
    .acceptPackages(basePackage)
    .scan()
    .getClassesWithMethodAnnotation(this.java)
    .flatMap { it.loadClass().methods.toList() }
    .mapNotNull { it.kotlinFunction }

val KClass<out Annotation>.annotatedMethods
    get() = ClassGraph()
        .enableMethodInfo()
        .also { find<Loader>().classLoaders.keys.forEach { classLoader -> it.addClassLoader(classLoader) } }
        .acceptPackages(*find<Loader>().classLoaders.values.toTypedArray())
        .scan()
        .getClassesWithMethodAnnotation(this.java)
        .flatMap { it.loadClass().methods.toList() }
        .mapNotNull { it.kotlinFunction }