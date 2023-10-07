@file:Suppress("unused")

package rocks.clanattack.util.extention

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Converts the given amount of ticks to a [Duration].
 */
val Int.ticks: Duration
    get() = (this / 20.0).seconds

/**
 * Converts the given amount of ticks to a [Duration].
 */
val Long.ticks: Duration
    get() = (this / 20.0).seconds

/**
 * Converts the given amount of ticks to a [Duration].
 */
val Double.ticks: Duration
    get() = (this / 20.0).seconds

/**
 * Converts the duration to ticks.
 */
val Duration.inWholeTicks: Long
    get() = this.inWholeSeconds * 20