package rocks.clanattack.impl.entry

import org.bukkit.plugin.java.JavaPlugin
import rocks.clanattack.java.RegistryHelper
import rocks.clanattack.impl.entry.plugin.Loader
import rocks.clanattack.impl.entry.point.PointHandler
import rocks.clanattack.impl.entry.service.ServiceHandler
import rocks.clanattack.impl.log.Logger
import rocks.clanattack.impl.minecraft.listener.ListenerHandler

@Suppress("unused")
class Boot : JavaPlugin() {

    override fun onLoad() {
        val registry = Registry(this)

        try {
            RegistryHelper.setRegistry(registry)
        } catch (_: Exception) {
            this.logger.severe("Could not set the registry.")
            this.server.shutdown()
            return
        }

        registry.add(Logger(this.logger))

        val loader = registry.create(Loader::class)
        loader.register(this.classLoader, "rocks.clanattack.impl")
    }

    override fun onEnable() {
        ListenerHandler.block()

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