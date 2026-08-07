package tj.horner.villagergpt.openai

import com.google.gson.JsonObject

data class Tool(
    val type: String = "function",
    val function: FunctionDefinition
)

data class FunctionDefinition(
    val name: String,
    val description: String,
    val parameters: JsonObject
)
