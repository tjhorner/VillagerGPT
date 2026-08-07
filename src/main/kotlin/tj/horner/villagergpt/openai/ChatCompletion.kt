package tj.horner.villagergpt.openai

import com.google.gson.annotations.SerializedName

data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val tools: List<Tool>? = null,
    val temperature: Double = 0.7,
    val user: String? = null,
    @SerializedName("reasoning_effort") val reasoningEffort: String? = null
)

data class ChatCompletionResponse(
    val choices: List<ChatCompletionChoice>
)

data class ChatCompletionChoice(
    val message: ChatMessage
)
