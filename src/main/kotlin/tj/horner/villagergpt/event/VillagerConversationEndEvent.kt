package tj.horner.villagergpt.event

import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class VillagerConversationEndEvent(val player: Player, val villager: Villager) : Event() {
    companion object {
        private val handlers = HandlerList()

        @Suppress("unused")
        @JvmStatic
        fun getHandlerList(): HandlerList = handlers
    }

    override fun getHandlers(): HandlerList {
        return VillagerConversationEndEvent.handlers
    }
}