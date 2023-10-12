package rocks.clanattack.minecraft.command

import rocks.clanattack.entry.Registry
import rocks.clanattack.player.Player

/**
 * Registers a method to be executed as a command handler.
 *
 * The command needs to be registered in the database in order to be executed.
 *
 * For restrictions on the containing class/object see [Registry.create].
 * The method must have a [Player] as it's first parameter and an [Array<out String>][Array] as it's second parameter.
 *
 * @param name The name of the command.
 */
annotation class Command(val name: String)

/**
 * Registers a method to be executed as a tab completer.
 *
 * The command needs to be registered in the database in order to be executed.
 *
 * For restrictions on the containing class/object see [Registry.create].
 * The method must have a [Player] as it's first parameter, an [Array<out String>][Array] as it's second parameter
 * and a [MutableList<String>][MutableList] as it's third parameter.
 *
 * The third parameter is the list of suggestions that will be sent to the player.
 *
 * @param name The name of the command.
 */
annotation class TabComplete(val name: String)