package rocks.clanattack.impl.language

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import rocks.clanattack.database.DatabaseService
import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.impl.language.model.Languages
import rocks.clanattack.language.Language
import rocks.clanattack.setting.SettingService
import rocks.clanattack.setting.get
import rocks.clanattack.language.LanguageService as Interface

@Register(definition = Interface::class, depends = [DatabaseService::class, SettingService::class])
class LanguageService : ServiceImplementation(), Interface {

    override val languages: List<Language>
        get() = transaction { Languages.selectAll().map { Language(it[Languages.id].value) } }

    override var defaultLanguage: Language
        get() = getLanguage(find<SettingService>().get<String>("core.language.default", "DE"))
        set(value) { find<SettingService>()["core.language.default"] = value.isoCode }

    private val _placeholders = mutableMapOf(
        "prefix" to "core.prefix",
    )

    override val placeholders: Map<String, String>
        get() = _placeholders.toMap()

    override fun enable() {
        find<SettingService>().registerSetting("core.language.default", "DE")
    }

    override fun getLanguage(isoCode: String): Language {
        val exists = transaction { Languages.select { Languages.id eq isoCode }.count() != 0L }
        if (!exists) throw IllegalArgumentException("Language with iso code $isoCode does not exist")

        return Language(isoCode)
    }

    override fun addPlaceholder(key: String, value: String) {
        _placeholders[key] = value
    }
}