package rocks.clanattack.impl.minecraft.command.model

data class WhitelistedCommand(val command: String, val permission: String, val aliases: List<String>)
