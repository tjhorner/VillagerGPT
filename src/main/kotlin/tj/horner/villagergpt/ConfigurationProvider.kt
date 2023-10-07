package tj.horner.villagergpt

interface ConfigurationProvider {
    val openAIKey: String
    val openAIModel: String?
    val logConversations: Boolean
    val preambleMessageType: String
}