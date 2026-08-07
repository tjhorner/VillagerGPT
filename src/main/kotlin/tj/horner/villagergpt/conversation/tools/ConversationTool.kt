package tj.horner.villagergpt.conversation.tools

import com.google.gson.JsonObject
import tj.horner.villagergpt.conversation.VillagerConversation
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageAction

interface ConversationTool {
    val name: String
    val description: String
    val parameters: JsonObject

    fun execute(argumentsJson: String, conversation: VillagerConversation): ToolCallResult
}

data class ToolCallResult(
    val resultForModel: String,
    val actions: Collection<ConversationMessageAction> = emptyList()
)
