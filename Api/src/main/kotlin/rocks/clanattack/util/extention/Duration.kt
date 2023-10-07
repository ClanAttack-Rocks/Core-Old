@file:Suppress("unused")

package rocks.clanattack.util.extention

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
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

/**
 * Converts the given amount of years to a [Duration].
 *
 * For this a year is expected as 365 days.
 */
val Int.years: Duration
    get() = (this * 365).days

/**
 * Converts the given amount of years to a [Duration].
 *
 * For this a year is expected as 365 days.
 */
val Long.years: Duration
    get() = (this * 365).days

/**
 * Converts the given amount of years to a [Duration].
 *
 * For this a year is expected as 365 days.
 */
val Double.years: Duration
    get() = (this * 365).days

/**
 * Converts the duration to years.
 *
 * For this a year is expected as 365 days.
 */
val Duration.inWholeYears: Long
    get() = this.inWholeDays / 365

/**
 * Converts the given amount of months to a [Duration].
 *
 * For this a month is expected as 30 days.
 */
val Int.months: Duration
    get() = (this * 30).days

/**
 * Converts the given amount of months to a [Duration].
 *
 * For this a month is expected as 30 days.
 */
val Long.months: Duration
    get() = (this * 30).days

/**
 * Converts the given amount of months to a [Duration].
 *
 * For this a month is expected as 30 days.
 */
val Double.months: Duration
    get() = (this * 30).days

/**
 * Converts the duration to months.
 *
 * For this a month is expected as 30 days.
 */
val Duration.inWholeMonths: Long
    get() = this.inWholeDays / 30

/**
 * Converts the given amount of weeks to a [Duration].
 */
val Int.weeks: Duration
    get() = (this * 7).days

/**
 * Converts the given amount of weeks to a [Duration].
 */
val Long.weeks: Duration
    get() = (this * 7).days

/**
 * Converts the given amount of weeks to a [Duration].
 */
val Double.weeks: Duration
    get() = (this * 7).days