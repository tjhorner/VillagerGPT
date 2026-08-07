package tj.horner.villagergpt.conversation.pipeline.actions

import tj.horner.villagergpt.conversation.VillagerConversation
import tj.horner.villagergpt.conversation.VillagerConversationManager
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageAction
import tj.horner.villagergpt.conversation.pipeline.EndsConversation

class EndConversationAction(
    private val conversationManager: VillagerConversationManager,
    private val conversation: VillagerConversation
) : ConversationMessageAction, EndsConversation {
    override fun run() {
        conversationManager.endConversation(conversation)
    }
}
