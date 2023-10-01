@file:Suppress("unused")

package rocks.clanattack.database

/**
 * A [Patch] to a specific record in a table.
 *
 * To view more information on json patches, see [https://jsonpatch.com/](https://jsonpatch.com/).
 */
sealed interface Patch {

    val op: String

}

/**
 * A [AddPatch] represents that some value was added to a record.
 */
data class AddPatch(val path: String, val value: Any) : Patch {

    override val op = "add"
}

/**
 * A [RemovePatch] represents that some value was removed from a record.
 */
data class RemovePatch(val path: String) : Patch {

    override val op = "remove"

}

/**
 * A [ReplacePatch] represents that some value was replaced in a record.
 */
data class ReplacePatch(val path: String, val value: Any) : Patch {

    override val op = "replace"

}

/**
 * A [ChangePatch] represents that some value was changed in a record.
 */
data class ChangePatch(val path: String, val value: Any) : Patch {

    override val op = "change"

}