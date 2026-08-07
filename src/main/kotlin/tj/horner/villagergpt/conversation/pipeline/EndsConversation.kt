package tj.horner.villagergpt.conversation.pipeline

/**
 * Marks a [ConversationMessageAction] that ends the conversation, so [ConversationPipeline] knows
 * to stop calling the producer for further rounds once one shows up.
 */
interface EndsConversation
