package rocks.clanattack.impl.util.format

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.language.Language
import rocks.clanattack.util.extention.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import rocks.clanattack.util.format.DateTimeService as Interface

@Register(definition = Interface::class)
class DateTimeService : ServiceImplementation(), Interface {

    private val durationRegex = """
        ^(?:(\d+)y[ ,]?)?
        (?:(\d+)mo[ ,]?)?
        (?:(\d+)w[ ,]?)?
        (?:(\d+)d[ ,]?)?
        (?:(\d+)h[ ,]?)?
        (?:(\d+)m[ ,]?)?
        (?:(\d+)s[ ,]?)?$
    """.trimIndent().replace("\n", "").toRegex()

    private val names = arrayOf(
        "core.time.year", "core.time.years",
        "core.time.month", "core.time.months",
        "core.time.day", "core.time.days",
        "core.time.hour", "core.time.hours",
        "core.time.minute", "core.time.minutes",
        "core.time.second", "core.time.seconds"
    )

    override fun parseDuration(duration: String): Duration {
        val match = durationRegex.matchEntire(duration) ?: return ZERO

        val years = (match.groupValues[1].toIntOrNull() ?: 0).years
        val months = (match.groupValues[2].toIntOrNull() ?: 0).months
        val weeks = (match.groupValues[3].toIntOrNull() ?: 0).weeks
        val days = (match.groupValues[4].toIntOrNull() ?: 0).days
        val hours = (match.groupValues[5].toIntOrNull() ?: 0).hours
        val minutes = (match.groupValues[6].toIntOrNull() ?: 0).minutes
        val seconds = (match.groupValues[7].toIntOrNull() ?: 0).seconds

        return years + months + weeks + days + hours + minutes + seconds
    }

    @Suppress("DuplicatedCode")
    private fun getUnits(duration: Duration): LongArray {
        if (duration == ZERO) return longArrayOf()

        var calculatedDuration = duration

        val years = calculatedDuration.inWholeYears.also { calculatedDuration -= it.years }
        val months = calculatedDuration.inWholeMonths.also { calculatedDuration -= it.months }
        val days = calculatedDuration.inWholeDays.also { calculatedDuration -= it.days }
        val hours = calculatedDuration.inWholeHours.also { calculatedDuration -= it.hours }
        val minutes = calculatedDuration.inWholeMinutes.also { calculatedDuration -= it.minutes }
        val seconds = calculatedDuration.inWholeSeconds.also { calculatedDuration -= it.seconds }

        return longArrayOf(years, months, days, hours, minutes, seconds)
    }

    override fun formatDuration(duration: Duration, language: Language, accuracy: UInt): ComponentLike {
        val units = getUnits(duration)

        if (units.isEmpty()) language.getMessage("core.time.now")

        val componentBuilder = Component.text()

        var currentAccuracy = 0u
        for (i in units.indices) {
            if (currentAccuracy >= accuracy && accuracy != 0u) break

            val difference = units[i]
            if (difference == 0L) continue

            if (currentAccuracy != 0u) componentBuilder.append(Component.text(" "))
            componentBuilder.append(
                Component.text(difference),
                Component.text(" "),
                language.getMessage(names[i * 2 + if (difference == 1L) 0 else 1])
            )
            currentAccuracy++
        }

        if (componentBuilder.children().isEmpty()) language.getMessage("core.time.now")
        return componentBuilder.build()
    }

}