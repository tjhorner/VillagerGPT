package tj.horner.villagergpt.bukkit

import org.bukkit.configuration.Configuration
import tj.horner.villagergpt.ConfigurationProvider

class BukkitConfigurationProvider(private val config: Configuration) : ConfigurationProvider {
    override val openAIKey: String
        get() = config.getString("openai-key")!!
    override val openAIModel: String?
        get() = config.getString("openai-model")
    override val logConversations: Boolean
        get() = config.getBoolean("log-conversations")
    override val preambleMessageType: String
        get() = config.getString("preamble-message-type")!!
}