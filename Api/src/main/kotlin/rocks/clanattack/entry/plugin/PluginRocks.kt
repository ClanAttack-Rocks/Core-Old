package rocks.clanattack.entry.plugin

import org.bukkit.plugin.java.JavaPlugin
import rocks.clanattack.entry.find
import rocks.clanattack.util.log.Logger

/**
 * A [PluginRocks] is a Bukkit plugin that uses the ClanAttack API.
 *
 * The plugin has, other than a standard bukkit plugin no load method.
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

    override fun onEnable() {
        try {
            enable()
        } catch (e: Exception) {
            find<Logger>().error("Cloud not enable the plugin ${this.name}", e)
        }
    }

    override fun onDisable() {
        try {
            disable()
        } catch (e: Exception) {
            find<Logger>().error("Cloud not disable the plugin ${this.name}", e)
        }
    }

    /**
     * Called when the [PluginRocks] is enabled.
     */
    open fun enable() {

    }

    /**
     * Called when the [PluginRocks] is disabled.
     */
    open fun disable() {

    }

}