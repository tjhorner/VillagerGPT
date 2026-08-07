package tj.horner.villagergpt.openai

import com.google.gson.annotations.SerializedName

object ChatRole {
    const val System = "system"
    const val User = "user"
    const val Assistant = "assistant"
    const val Tool = "tool"
}

data class ChatMessage(
    val role: String,
    val content: String? = null,
    @SerializedName("tool_calls") val toolCalls: List<ToolCall>? = null,
    @SerializedName("tool_call_id") val toolCallId: String? = null
)

data class ToolCall(
    val id: String,
    val type: String = "function",
    val function: FunctionCall
)

data class FunctionCall(
    val name: String,
    val arguments: String
)
