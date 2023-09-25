package rocks.clanattack.entry.plugin

/**
 * The [Loader] is used to register [PluginRocks], witch are plugins create for ClanAttack.
 *
 * The [Loader] stores the [ClassLoader] of every plugin, to search for annotations,
 * in addition to a base package, in witch the [Loader] will search for annotations.
 */
interface Loader {

    /**
     * All [ClassLoader]s and there base packages.
     */
    val classLoaders: Map<ClassLoader, String>

    /**
     * Registers the given [classLoader] and its base package.
     */
    fun register(classLoader: ClassLoader, basePackage: String)

    /**
     * Registers a listener that will be called when a new [ClassLoader] is registered.
     */
    fun onRegister(listener: (ClassLoader, String) -> Unit)

}