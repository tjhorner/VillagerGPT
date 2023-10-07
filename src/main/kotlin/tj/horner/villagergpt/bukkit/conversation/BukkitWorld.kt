package tj.horner.villagergpt.bukkit.conversation

import org.bukkit.World
import tj.horner.villagergpt.conversation.interfaces.AbstractWorld

class BukkitWorld(val world: World) : AbstractWorld {
    override val isStormy: Boolean
        get() = world.hasStorm()

    override val isDayTime: Boolean
        get() = world.isDayTime
}