package tj.horner.villagergpt.conversation.pipeline

import tj.horner.villagergpt.conversation.VillagerConversation
import tj.horner.villagergpt.conversation.tools.ConversationTool
import tj.horner.villagergpt.openai.ChatMessage

interface ConversationMessageProducer {
    suspend fun produceNextMessage(conversation: VillagerConversation, tools: List<ConversationTool>): ChatMessage
}
