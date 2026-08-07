package tj.horner.villagergpt.conversation.pipeline.producers

import org.bukkit.configuration.Configuration
import tj.horner.villagergpt.conversation.VillagerConversation
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageProducer
import tj.horner.villagergpt.conversation.tools.ConversationTool
import tj.horner.villagergpt.openai.ChatCompletionRequest
import tj.horner.villagergpt.openai.ChatMessage
import tj.horner.villagergpt.openai.FunctionDefinition
import tj.horner.villagergpt.openai.OpenAIClient
import tj.horner.villagergpt.openai.Tool

class OpenAIMessageProducer(config: Configuration) : ConversationMessageProducer {
    private val client = OpenAIClient(
        apiKey = config.getString("openai-key")!!,
        baseUrl = config.getString("openai-base-url") ?: "https://api.openai.com/v1"
    )

    private val model = config.getString("openai-model") ?: "gpt-4o-mini"
    private val reasoningEffort = config.getString("openai-reasoning-effort")?.trim()?.takeUnless { it.isEmpty() }

    override suspend fun produceNextMessage(conversation: VillagerConversation, tools: List<ConversationTool>): ChatMessage {
        val request = ChatCompletionRequest(
            model = model,
            messages = conversation.messages,
            tools = tools.map { it.toToolDefinition() },
            temperature = 0.7,
            user = conversation.player.uniqueId.toString(),
            reasoningEffort = reasoningEffort
        )

        val completion = client.createChatCompletion(request)
        return completion.choices[0].message
    }

    private fun ConversationTool.toToolDefinition() = Tool(
        function = FunctionDefinition(
            name = name,
            description = description,
            parameters = parameters
        )
    )
}
