package rocks.clanattack.entry.plugin

import org.bukkit.plugin.java.JavaPlugin
import rocks.clanattack.entry.find
import rocks.clanattack.log.Logger
import java.lang.Exception

/**
 * A [PluginRocks] is a Bukkit plugin that uses the ClanAttack API.
 *
 * The plugin should only be used for registering its class loader.
 * Every other action should be done via services, entry and exit points.
 */
open class PluginRocks : JavaPlugin() {

    /**
     * The [basePackage] of the plugin, in witch the system will search for annotations.
     *
     * Per default the [basePackage] is the package in witch the [PluginRocks] is located.
     */
    open val basePackage: String = this.javaClass.`package`.name

    override fun onLoad() {
        try {
            find<Loader>().register(this.classLoader, this.basePackage)
        } catch (e: Exception) {
            find<Logger>().error("Could not register the plugin ${this.name}", e)
        }
    }
}