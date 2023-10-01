package rocks.clanattack.database

/**
 * The type of a change in a live query.
 */
@Suppress("unused")
enum class ChangeType {

    /**
     * The object was created.
     */
    CREATE,

    /**
     * The object was updated.
     */
    UPDATE,
    
    /**
     * The object was deleted.
     */
    DELETE

}