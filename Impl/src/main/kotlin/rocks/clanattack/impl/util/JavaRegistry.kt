package rocks.clanattack.impl.util

import rocks.clanattack.entry.registry

@Suppress("unused")
object JavaRegistry {

    @JvmStatic
    fun <T : Any> findOrNull(clazz: Class<T>) = registry[clazz.kotlin]

    @JvmStatic
    fun <T : Any> find(clazz: Class<T>) =
        registry[clazz.kotlin] ?: throw NullPointerException("No instance of ${clazz.simpleName} was found.")

    @JvmStatic
    fun <T : Any> getOrCreate(clazz: Class<T>) = registry.getOrCreate(clazz.kotlin)

    @JvmStatic
    fun getRegistry() = registry

}