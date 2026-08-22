package com.example.arsipbpkpad.data.remote.dto





data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Double = 0.0,
     val maxTokens: Int = 1024,
     val responseFormat: GroqResponseFormat? = null
)


data class GroqMessage(
    val role: String,
    val content: String
)


data class GroqResponseFormat(
    val type: String
)


data class GroqResponse(
    val id: String,
    val choices: List<GroqChoice>
)


data class GroqChoice(
    val message: GroqMessage,
     val finishReason: String
)
