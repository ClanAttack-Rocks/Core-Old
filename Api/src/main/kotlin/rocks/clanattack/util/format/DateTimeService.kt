package rocks.clanattack.util.format

import rocks.clanattack.entry.service.Service
import rocks.clanattack.language.Language
import kotlin.time.Duration

/**
 * The [DateTimeService] is used to format dates and times.
 */
interface DateTimeService : Service {

    /**
     * Formats the given [duration] (as a string) to a [Duration].
     *
     * The format is a chain of numbers, followed by a unit of time.
     * These are always in the format of `(number)(unit)`.
     *
     * For this a year is expected as 365 days and a month as 30 days.
     *
     * | Unit | Description |
     * | ---- | ----------- |
     * | `y`  | Years       |
     * | `mo` | Months      |
     * | `w`  | Weeks       |
     * | `d`  | Days        |
     * | `h`  | Hours       |
     * | `m`  | Minutes     |
     * | `s`  | Seconds     |
     */
    fun parseDuration(duration: String): Duration

    /**
     * Formats the given [duration] to a string, using the given [language].
     *
     * The [accuracy] is the amount of units of time, that should be shown.
     *
     * For example, an [accuracy] of 2 would be `2 years and 3 months`,
     * while an [accuracy] of 3 would be `2 years, 3 months and 4 weeks`.
     *
     * The [accuracy] can be set to -1, to show all units of time,
     * if a unit of time is 0, it will not be shown.
     *
     * For this a year is expected as 365 days and a month as 30 days.
     */
    fun formatDuration(duration: Duration, language: Language, accuracy: Int = 2): String

}