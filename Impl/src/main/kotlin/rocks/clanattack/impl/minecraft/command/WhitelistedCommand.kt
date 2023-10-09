package rocks.clanattack.impl.minecraft.command

data class WhitelistedCommand(val command: String, val permission: String, val aliases: List<String>)
