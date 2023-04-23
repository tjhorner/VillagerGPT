package tj.horner.villagergpt

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.github.shynixn.mccoroutine.bukkit.setSuspendingExecutor
import tj.horner.villagergpt.command.ClearCommand
import tj.horner.villagergpt.command.EndCommand
import tj.horner.villagergpt.command.TalkCommand
import tj.horner.villagergpt.conversation.VillagerConversationManager
import tj.horner.villagergpt.conversation.pipeline.MessageProcessorPipeline
import tj.horner.villagergpt.conversation.pipeline.processor.ActionProcessor
import tj.horner.villagergpt.conversation.pipeline.processor.TradeOfferProcessor
import tj.horner.villagergpt.conversation.pipeline.producer.OpenAIMessageProducer
import tj.horner.villagergpt.handler.ConversationEventsHandler
import tj.horner.villagergpt.task.EndStaleConversationsTask

class VillagerGPT : SuspendingJavaPlugin() {
    val conversationManager = VillagerConversationManager(this)
    val messagePipeline = MessageProcessorPipeline(
        OpenAIMessageProducer(config),
        listOf(
            ActionProcessor(),
            TradeOfferProcessor()
        )
    )

    override suspend fun onEnableAsync() {
        saveDefaultConfig()
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
        server.pluginManager.registerSuspendingEvents(ConversationEventsHandler(this), this)
    }

    private fun scheduleTasks() {
        EndStaleConversationsTask(this).runTaskTimer(this, 0L, 200L)
    }
}
