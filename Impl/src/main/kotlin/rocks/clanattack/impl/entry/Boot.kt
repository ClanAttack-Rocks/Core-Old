package rocks.clanattack.impl.entry

import org.bukkit.plugin.java.JavaPlugin
import rocks.clanattack.impl.entry.plugin.Loader
import rocks.clanattack.impl.entry.point.PointHandler
import rocks.clanattack.impl.entry.service.ServiceHandler
import rocks.clanattack.impl.util.log.Logger
import rocks.clanattack.java.RegistryHelper

@Suppress("unused")
class Boot : JavaPlugin() {

    override fun onLoad() {
        this.logger.info("Loading ClanAttack Core...")
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
        this.logger.info("Loaded ClanAttack Core.")
    }

    override fun onEnable() {
        ServiceHandler.registerServices()
        ServiceHandler.enableServices()

        PointHandler.callEntryPoints()
    }

    override fun onDisable() {
        PointHandler.callExitPoints()

        ServiceHandler.disableServices()
    }

}