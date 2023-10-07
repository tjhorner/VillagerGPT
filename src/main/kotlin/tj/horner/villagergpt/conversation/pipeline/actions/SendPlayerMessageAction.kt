package tj.horner.villagergpt.conversation.pipeline.actions

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import tj.horner.villagergpt.conversation.interfaces.AbstractPlayer
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageAction

class SendPlayerMessageAction(private val player: AbstractPlayer, private val message: Component) : ConversationMessageAction {
    override fun run() {
        player.sendMessage(message)
    }
}