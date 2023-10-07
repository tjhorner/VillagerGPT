package tj.horner.villagergpt.conversation.pipeline.actions

import tj.horner.villagergpt.conversation.interfaces.AbstractVillager
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageAction

class ShakeHeadAction(private val villager: AbstractVillager) : ConversationMessageAction {
    override fun run() {
        villager.shakeHead()
    }
}