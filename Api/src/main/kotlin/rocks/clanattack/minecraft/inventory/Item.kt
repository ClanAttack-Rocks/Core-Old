package rocks.clanattack.minecraft.inventory

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * Modifies the item meta of this item stack using the given [block].
 *
 * If the item has no meta or the meta is not of type [T], a [IllegalStateException] will be thrown.
 *
 * @throws IllegalStateException if the item has no meta or the meta is not of type [T]
 */
@Throws(IllegalStateException::class)
inline fun <reified T : ItemMeta, U> ItemStack.meta(crossinline block: T.() -> U): U {
    val meta = itemMeta as? T ?: throw IllegalStateException("Item has no meta or meta is not of type ${T::class.simpleName}")
    val result = meta.block()
    itemMeta = meta

    return result
}

/**
 * Modifies the item meta of this item stack using the given [block] and returns the modified item stack.
 *
 * If the item has no meta or the meta is not of type [T], a [IllegalStateException] will be thrown.
 *
 * @throws IllegalStateException if the item has no meta or the meta is not of type [T]
 */
@Throws(IllegalStateException::class)
inline fun <reified T : ItemMeta> ItemStack.modifyMeta(crossinline block: T.() -> Unit): ItemStack {
    meta<T, Unit> { block() }
    return this
}

/**
 * Converts an [ItemStack] to an [GuiItem]
 */
fun ItemStack.gui() = GuiItem(this)

/**
 * Converts an [ItemStack] to an [GuiItem] and adds and click listener to it.
 */
fun ItemStack.gui(click: (InventoryClickEvent) -> Unit) = GuiItem(this, click)