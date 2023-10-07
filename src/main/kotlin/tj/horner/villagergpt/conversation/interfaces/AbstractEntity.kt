package tj.horner.villagergpt.conversation.interfaces

interface AbstractEntity {
    val id: String
    val name: String
    val currentBiomeName: String
    val world: AbstractWorld
    val stableRandomSeed: Long

    fun isFarAwayFrom(otherEntity: AbstractEntity): Boolean
    fun isInSameWorld(otherEntity: AbstractEntity): Boolean
    fun playSound(soundName: String, emitter: AbstractEntity)
}