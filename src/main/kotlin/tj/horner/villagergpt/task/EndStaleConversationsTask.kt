package tj.horner.villagergpt.task

import org.bukkit.scheduler.BukkitRunnable
import tj.horner.villagergpt.VillagerGPT

class EndStaleConversationsTask(private val plugin: VillagerGPT) : BukkitRunnable() {
    override fun run() {
        plugin.conversationManager.endStaleConversations()
    }
}