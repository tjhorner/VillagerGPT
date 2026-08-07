package tj.horner.villagergpt.events

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import tj.horner.villagergpt.conversation.VillagerConversation
import tj.horner.villagergpt.openai.ChatMessage

class VillagerConversationMessageEvent(val conversation: VillagerConversation, val message: ChatMessage) : Event(!Bukkit.isPrimaryThread()) {
    companion object {
        private val handlers = HandlerList()

        @Suppress("unused")
        @JvmStatic
        fun getHandlerList() = handlers
    }

    override fun getHandlers(): HandlerList {
        return VillagerConversationMessageEvent.handlers
    }
}