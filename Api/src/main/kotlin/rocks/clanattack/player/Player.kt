package rocks.clanattack.player

import rocks.clanattack.player.trait.DisplayTrait
import rocks.clanattack.player.trait.PermissionTrait
import rocks.clanattack.player.trait.communication.CommunicationTrait
import java.util.*

interface Player {

    val uuid: UUID

    val name: String

    val display: DisplayTrait

    val permission: PermissionTrait

    val communication: CommunicationTrait

}