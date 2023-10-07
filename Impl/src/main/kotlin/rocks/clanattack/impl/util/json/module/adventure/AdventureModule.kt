package rocks.clanattack.impl.util.json.module.adventure

import com.fasterxml.jackson.databind.module.SimpleModule
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

object AdventureModule : SimpleModule() {

    init {
        addSerializer(Component::class.java, ComponentSerializer())
        addDeserializer(Component::class.java, ComponentDeserializer())

        addSerializer(TextColor::class.java, TextColorSerializer())
        addDeserializer(TextColor::class.java, TextColorDeserializer())
    }

    private fun readResolve(): Any = AdventureModule

}