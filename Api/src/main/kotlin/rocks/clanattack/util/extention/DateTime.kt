package rocks.clanattack.util.extention

import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Gets the difference between this [LocalDateTime] and the [other] [LocalDateTime].
 */
fun LocalDateTime.diff(other: LocalDateTime): Duration {
    val start = this.toInstant(TimeZone.UTC).toEpochMilliseconds()
    val end = other.toInstant(TimeZone.UTC).toEpochMilliseconds()
    return (end - start).milliseconds
}

/**
 * Gets the current [LocalDateTime]
 */
fun LocalDateTime.Companion.now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())