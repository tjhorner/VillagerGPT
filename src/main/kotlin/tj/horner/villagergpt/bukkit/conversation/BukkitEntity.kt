package tj.horner.villagergpt.bukkit.conversation

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Entity
import tj.horner.villagergpt.conversation.interfaces.AbstractEntity
import tj.horner.villagergpt.conversation.interfaces.AbstractWorld

open class BukkitEntity(val entity: Entity) : AbstractEntity {
    override val id: String
        get() = entity.uniqueId.toString()

    override val name: String
        get() = entity.name

    override val currentBiomeName: String
        get() = entity.world.getBiome(entity.location).name

    override val world: AbstractWorld
        get() = BukkitWorld(entity.world)

    override val stableRandomSeed: Long
        get() = entity.uniqueId.mostSignificantBits

    override fun isFarAwayFrom(otherEntity: AbstractEntity): Boolean {
        val otherBukkitEntity = (otherEntity as BukkitEntity).entity

        val radius = 20.0 // blocks?
        val radiusSquared = radius * radius
        val distanceSquared = otherBukkitEntity.location.distanceSquared(entity.location)
        return distanceSquared > radiusSquared
    }

    override fun isInSameWorld(otherEntity: AbstractEntity): Boolean {
        val otherBukkitEntity = (otherEntity as BukkitEntity).entity
        return entity.location.world == otherBukkitEntity.location.world
    }

    override fun playSound(soundName: String, emitter: AbstractEntity) {
        val bukkitEmitter = (emitter as BukkitEntity).entity
        val sound = Sound.sound(Key.key(soundName), Sound.Source.NEUTRAL, 1f, 1f)
        entity.playSound(sound, bukkitEmitter)
    }
}