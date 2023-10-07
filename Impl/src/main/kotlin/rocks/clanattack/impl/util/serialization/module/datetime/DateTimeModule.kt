package rocks.clanattack.impl.util.serialization.module.datetime

import com.fasterxml.jackson.databind.module.SimpleModule
import kotlinx.datetime.*
import rocks.clanattack.impl.util.serialization.module.datetime.datetime.*
import rocks.clanattack.impl.util.serialization.module.datetime.periode.DatePeriodDeserializer
import rocks.clanattack.impl.util.serialization.module.datetime.periode.DatePeriodSerializer
import rocks.clanattack.impl.util.serialization.module.datetime.periode.DateTimePeriodDeserializer
import rocks.clanattack.impl.util.serialization.module.datetime.periode.DateTimePeriodSerializer
import rocks.clanattack.impl.util.serialization.module.datetime.zone.FixedOffsetTimeZoneDeserializer
import rocks.clanattack.impl.util.serialization.module.datetime.zone.TimeZoneDeserializer
import rocks.clanattack.impl.util.serialization.module.datetime.zone.TimeZoneSerializer

object DateTimeModule : SimpleModule() {

    init {
        addSerializer(Instant::class.java, InstantSerializer())
        addDeserializer(Instant::class.java, InstantDeserializer())

        addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer())
        addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer())
        addSerializer(LocalDate::class.java, LocalDateSerializer())
        addDeserializer(LocalDate::class.java, LocalDateDeserializer())
        addSerializer(LocalTime::class.java, LocalTimeSerializer())
        addDeserializer(LocalTime::class.java, LocalTimeDeserializer())

        addDeserializer(FixedOffsetTimeZone::class.java, FixedOffsetTimeZoneDeserializer())
        addSerializer(TimeZone::class.java, TimeZoneSerializer())
        addDeserializer(TimeZone::class.java, TimeZoneDeserializer())

        addSerializer(DateTimePeriod::class.java, DateTimePeriodSerializer())
        addDeserializer(DateTimePeriod::class.java, DateTimePeriodDeserializer())
        addSerializer(DatePeriod::class.java, DatePeriodSerializer())
        addDeserializer(DatePeriod::class.java, DatePeriodDeserializer())
    }

    private fun readResolve(): Any = DateTimeModule

}