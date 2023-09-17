package rocks.clanattack.impl.entry

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import rocks.clanattack.entry.plugin.Loader
import rocks.clanattack.impl.entry.plugin.LoaderImpl
import rocks.clanattack.impl.entry.point.PointHandler
import rocks.clanattack.impl.entry.service.ServiceHandler
import rocks.clanattack.impl.java.Reflection
import rocks.clanattack.impl.util.log.LoggerImpl
import rocks.clanattack.impl.util.minecraft.listener.ListenerHandler
import rocks.clanattack.util.log.Logger

class Boot : JavaPlugin() {

    override fun onLoad() {
        ListenerHandler.block()

        val registry = RegistryImpl()
        try {
            Reflection.setRegistry(registry)
        } catch (e: Exception) {
            this.logger.severe("Could not set the registry.")
        }

        registry.set(Plugin::class, this)
        registry.set(Logger::class, LoggerImpl(this.logger))

        val loader = registry.create(Loader::class, LoaderImpl::class)
        loader.register(this.classLoader, "rocks.clanattack.impl")
    }

    override fun onEnable() {
        ServiceHandler.registerServices()
        ServiceHandler.enableServices()

        PointHandler.callEntryPoints()

        ListenerHandler.load()
    }

    override fun onDisable() {
        PointHandler.callExitPoints()

        ServiceHandler.disableServices()
    }

}