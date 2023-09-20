package rocks.clanattack.library

import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Library : JavaPlugin() {

    override fun onLoad() {
        logger.info("Libraries for ClanAttack rock!")
    }

}