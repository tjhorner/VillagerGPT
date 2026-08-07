package tj.horner.villagergpt.openai

import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class OpenAIRequestException(val statusCode: Int, message: String) : Exception(message)

/**
 * A minimal client for the OpenAI chat completions API (and any API compatible
 * with it, e.g. Azure OpenAI, OpenRouter, Ollama, LM Studio, vLLM).
 */
class OpenAIClient(private val apiKey: String, baseUrl: String) {
    private val gson = Gson()

    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(30))
        .build()

    private val chatCompletionsUri = URI.create(baseUrl.trimEnd('/') + "/chat/completions")

    suspend fun createChatCompletion(request: ChatCompletionRequest): ChatCompletionResponse {
        val requestBody = gson.toJson(request)

        val httpRequest = HttpRequest.newBuilder(chatCompletionsUri)
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(60))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        val response = withContext(Dispatchers.IO) {
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        }

        if (response.statusCode() !in 200..299) {
            throw OpenAIRequestException(response.statusCode(), describeError(response.body()))
        }

        return gson.fromJson(response.body(), ChatCompletionResponse::class.java)
    }

    private fun describeError(responseBody: String): String {
        return try {
            JsonParser.parseString(responseBody).asJsonObject
                .getAsJsonObject("error")
                .get("message").asString
        } catch (e: Exception) {
            responseBody
        }
    }
}
