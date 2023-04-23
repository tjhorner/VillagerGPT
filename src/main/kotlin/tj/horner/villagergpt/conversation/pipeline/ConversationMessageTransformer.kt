package tj.horner.villagergpt.conversation.pipeline

import tj.horner.villagergpt.conversation.VillagerConversation

interface ConversationMessageTransformer {
    fun transformMessage(message: String, conversation: VillagerConversation): String
}