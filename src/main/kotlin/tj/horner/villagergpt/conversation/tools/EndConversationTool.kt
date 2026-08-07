package tj.horner.villagergpt.conversation.tools

import com.google.gson.JsonObject
import tj.horner.villagergpt.conversation.VillagerConversation
import tj.horner.villagergpt.conversation.VillagerConversationManager
import tj.horner.villagergpt.conversation.pipeline.actions.EndConversationAction

class EndConversationTool(private val conversationManager: VillagerConversationManager) : ConversationTool {
    override val name = "end_conversation"
    override val description = """
        End the conversation with the player, e.g. because they said goodbye or you no longer wish to speak with
        them. This does not say anything to the player on its own, so you MUST call it only after you've already
        included your final goodbye in your reply text for this same message. Never call this before that.
    """.trimIndent()

    override val parameters = JsonObject().apply {
        addProperty("type", "object")
        add("properties", JsonObject())
    }

    override fun execute(argumentsJson: String, conversation: VillagerConversation): ToolCallResult {
        return ToolCallResult(
            resultForModel = "The conversation has been ended.",
            actions = listOf(EndConversationAction(conversationManager, conversation))
        )
    }
}
