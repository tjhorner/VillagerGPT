package tj.horner.villagergpt.conversation.pipeline

import net.kyori.adventure.text.Component
import tj.horner.villagergpt.conversation.VillagerConversation
import tj.horner.villagergpt.conversation.formatting.MessageFormatter
import tj.horner.villagergpt.conversation.pipeline.actions.SendPlayerMessageAction
import tj.horner.villagergpt.conversation.tools.ConversationTool
import tj.horner.villagergpt.conversation.tools.ToolCallResult
import tj.horner.villagergpt.openai.ChatMessage
import tj.horner.villagergpt.openai.ChatRole
import tj.horner.villagergpt.openai.ToolCall

/**
 * Drives a single player message through the OpenAI tool-calling loop: the producer is asked for
 * the villager's next message, and if it calls any tools, those are executed and their results are
 * fed back to the producer until it responds with plain text (or the round limit is hit).
 */
class ConversationPipeline(
    private val producer: ConversationMessageProducer,
    private val tools: List<ConversationTool>
) {
    companion object {
        private const val MAX_TOOL_CALL_ROUNDS = 5
    }

    suspend fun run(playerMessage: String, conversation: VillagerConversation): Iterable<ConversationMessageAction> {
        return runFrom(ChatMessage(role = ChatRole.User, content = playerMessage), conversation)
    }

    /**
     * Injects a system-generated notification about something that happened in the game (e.g. the
     * player completing a trade) and lets the villager react to it, without the player having said
     * anything.
     */
    suspend fun reactToEvent(eventDescription: String, conversation: VillagerConversation): Iterable<ConversationMessageAction> {
        return runFrom(ChatMessage(role = ChatRole.System, content = "[EVENT] $eventDescription"), conversation)
    }

    private suspend fun runFrom(message: ChatMessage, conversation: VillagerConversation): Iterable<ConversationMessageAction> {
        val messageCountBeforeRun = conversation.messages.size
        conversation.addMessage(message)

        try {
            return runToolCallLoop(conversation)
        } catch (e: Exception) {
            while (conversation.messages.size > messageCountBeforeRun) {
                conversation.removeLastMessage()
            }
            throw e
        }
    }

    private suspend fun runToolCallLoop(conversation: VillagerConversation): Iterable<ConversationMessageAction> {
        val actions = mutableListOf<ConversationMessageAction>()

        repeat(MAX_TOOL_CALL_ROUNDS) {
            val response = producer.produceNextMessage(conversation, tools)
            conversation.addMessage(response)
            addFinalReplyAction(response, conversation, actions)

            val toolCalls = response.toolCalls
            if (toolCalls.isNullOrEmpty()) {
                return actions
            }

            toolCalls.forEach { toolCall ->
                val result = invokeTool(toolCall, conversation)
                actions.addAll(result.actions)

                conversation.addMessage(
                    ChatMessage(role = ChatRole.Tool, toolCallId = toolCall.id, content = result.resultForModel)
                )
            }

            if (actions.any { it is EndsConversation }) {
                return actions
            }
        }

        return actions
    }

    private fun addFinalReplyAction(
        response: ChatMessage,
        conversation: VillagerConversation,
        actions: MutableList<ConversationMessageAction>
    ) {
        val content = response.content?.trim()
        if (content.isNullOrEmpty()) return

        val formattedMessage = MessageFormatter.formatMessageFromVillager(Component.text(content), conversation.villager)
        actions.add(SendPlayerMessageAction(conversation.player, formattedMessage))
    }

    private fun invokeTool(toolCall: ToolCall, conversation: VillagerConversation): ToolCallResult {
        val tool = tools.firstOrNull { it.name == toolCall.function.name }
            ?: return ToolCallResult("Error: no tool named '${toolCall.function.name}' exists.")

        return try {
            tool.execute(toolCall.function.arguments, conversation)
        } catch (e: Exception) {
            ToolCallResult("Error: ${e.message}")
        }
    }
}
