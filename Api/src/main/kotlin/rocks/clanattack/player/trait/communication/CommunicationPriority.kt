package rocks.clanattack.player.trait.communication

/**
 * The priority of a communication with a player.
 */
enum class CommunicationPriority {

    /**
     * The lowest priority.
     */
    LOWEST,

    /**
     * A low priority.
     */
    LOW,

    /**
     * The default priority.
     */
    NORMAL,

    /**
     * A high priority.
     */
    HIGH,

    /**
     * The highest priority.
     */
    HIGHEST

}