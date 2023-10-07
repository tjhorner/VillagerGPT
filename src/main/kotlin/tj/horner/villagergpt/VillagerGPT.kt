package tj.horner.villagergpt

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.github.shynixn.mccoroutine.bukkit.setSuspendingExecutor
import org.bukkit.event.Listener
import tj.horner.villagergpt.bukkit.BukkitConfigurationProvider
import tj.horner.villagergpt.commands.ClearCommand
import tj.horner.villagergpt.commands.EndCommand
import tj.horner.villagergpt.commands.TalkCommand
import tj.horner.villagergpt.conversation.VillagerConversationManager
import tj.horner.villagergpt.conversation.pipeline.MessageProcessorPipeline
import tj.horner.villagergpt.conversation.pipeline.processors.ActionProcessor
import tj.horner.villagergpt.conversation.pipeline.processors.TradeOfferProcessor
import tj.horner.villagergpt.conversation.pipeline.producers.OpenAIMessageProducer
import tj.horner.villagergpt.handlers.ConversationEventsHandler
import tj.horner.villagergpt.tasks.EndStaleConversationsTask
import java.util.logging.Level

class VillagerGPT : SuspendingJavaPlugin() {
    private val configurationProvider = BukkitConfigurationProvider(config)

    val conversationManager = VillagerConversationManager(this, configurationProvider)
    val messagePipeline = MessageProcessorPipeline(
        OpenAIMessageProducer(config),
        listOf(
            ActionProcessor(),
            TradeOfferProcessor(logger)
        )
    )

    override suspend fun onEnableAsync() {
        saveDefaultConfig()

        if (!validateConfig()) {
            logger.log(Level.WARNING, "VillagerGPT has not been configured correctly! Please set the `openai-key` in config.yml.")
            return
        }

        setCommandExecutors()
        registerEvents()
        scheduleTasks()
    }

    override fun onDisable() {
        logger.info("Ending all conversations")
        conversationManager.endAllConversations()
    }

    private fun setCommandExecutors() {
        getCommand("ttv")!!.setSuspendingExecutor(TalkCommand(this))
        getCommand("ttvclear")!!.setSuspendingExecutor(ClearCommand(this))
        getCommand("ttvend")!!.setSuspendingExecutor(EndCommand(this))
    }

    private fun registerEvents() {
        val eventHandlers = listOf<Listener>(
                ConversationEventsHandler(this, configurationProvider, logger)
        )

        eventHandlers.forEach {
            server.pluginManager.registerSuspendingEvents(it, this)
        }
    }

    private fun scheduleTasks() {
        EndStaleConversationsTask(this).runTaskTimer(this, 0L, 200L)
    }

    private fun validateConfig(): Boolean {
        val openAiKey = config.getString("openai-key") ?: return false
        return openAiKey.trim() != ""
    }
}
