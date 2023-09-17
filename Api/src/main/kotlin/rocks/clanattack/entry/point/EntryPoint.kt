package rocks.clanattack.entry.point

import rocks.clanattack.entry.Registry
import rocks.clanattack.entry.service.Service

/**
 * A [EntryPoint] is the start of the execution of a plugin.
 *
 * For a method to be called as an entry point, it must be annotated.
 *
 * The method must be in a class eligible for an automatic [Registry.create] call **and** return nothing.
 *
 * The method can be private, but must be accessible.
 *
 * Every method will be called after all [Service]s are enabled.
 *
 * ```
 * @EntryPoint
 * fun onEnable() {
 *    // This method will be called when the plugin is enabled.
 * }
 * ```
 */
annotation class EntryPoint
