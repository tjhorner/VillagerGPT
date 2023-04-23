package tj.horner.villagergpt.conversation.pipeline

import tj.horner.villagergpt.conversation.VillagerConversation

interface ConversationMessageProcessor {
    fun processMessage(message: String, conversation: VillagerConversation): Collection<ConversationMessageAction>?
}