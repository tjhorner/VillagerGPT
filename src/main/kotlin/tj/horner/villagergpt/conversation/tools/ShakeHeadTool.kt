package tj.horner.villagergpt.conversation.tools

import com.google.gson.JsonObject
import tj.horner.villagergpt.conversation.VillagerConversation
import tj.horner.villagergpt.conversation.pipeline.actions.ShakeHeadAction

class ShakeHeadTool : ConversationTool {
    override val name = "shake_head"
    override val description = "Shake your head at the player, e.g. to express disagreement or refusal"
    override val parameters = JsonObject().apply {
        addProperty("type", "object")
        add("properties", JsonObject())
    }

    override fun execute(argumentsJson: String, conversation: VillagerConversation): ToolCallResult {
        return ToolCallResult(
            resultForModel = "You shook your head.",
            actions = listOf(ShakeHeadAction(conversation.villager))
        )
    }
}
