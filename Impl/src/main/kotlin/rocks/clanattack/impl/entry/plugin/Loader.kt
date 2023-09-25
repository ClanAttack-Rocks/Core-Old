package rocks.clanattack.impl.entry.plugin

import rocks.clanattack.entry.plugin.Loader as Interface
import rocks.clanattack.entry.service.ServiceImplementation

class Loader : ServiceImplementation(), Interface {

    private val _classLoaders = mutableMapOf<ClassLoader, String>()
    override val classLoaders: Map<ClassLoader, String>
        get() = _classLoaders.toMap()

    private val listeners = mutableListOf<(ClassLoader, String) -> Unit>()

    override fun register(classLoader: ClassLoader, basePackage: String) {
        _classLoaders[classLoader] = basePackage
        listeners.forEach { it(classLoader, basePackage) }
    }

    override fun onRegister(listener: (ClassLoader, String) -> Unit) {
        listeners.add(listener)
    }

}