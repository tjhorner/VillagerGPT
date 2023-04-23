package tj.horner.villagergpt.conversation.pipeline

import tj.horner.villagergpt.conversation.VillagerConversation

interface ConversationMessageProducer {
    suspend fun produceNextMessage(conversation: VillagerConversation): String
}