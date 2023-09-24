package rocks.clanattack.util.minecraft

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * Modifies the item meta of this item stack using the given [block].
 *
 * If the item has no meta or the meta is not of type [T], the [block] will not be executed.
 */
inline fun <reified T : ItemMeta> ItemStack.modifyMeta(crossinline block: T.() -> Unit) = also {
    val meta = itemMeta as? T ?: return@also
    meta.block()
    itemMeta = meta
}