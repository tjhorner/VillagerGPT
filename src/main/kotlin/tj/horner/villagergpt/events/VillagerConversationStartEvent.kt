package tj.horner.villagergpt.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import tj.horner.villagergpt.conversation.VillagerConversation

class VillagerConversationStartEvent(val conversation: VillagerConversation) : Event() {
    companion object {
        private val handlers = HandlerList()

        @Suppress("unused")
        @JvmStatic
        fun getHandlerList() = handlers
    }

    override fun getHandlers(): HandlerList {
        return VillagerConversationStartEvent.handlers
    }
}