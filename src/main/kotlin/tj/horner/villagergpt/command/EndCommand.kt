package tj.horner.villagergpt.command

import com.github.shynixn.mccoroutine.bukkit.SuspendingCommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tj.horner.villagergpt.VillagerGPT
import tj.horner.villagergpt.chat.ChatMessageTemplate

class EndCommand(private val plugin: VillagerGPT) : SuspendingCommandExecutor {
    override suspend fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return true
        val player: Player = sender

        val conversation = plugin.conversationManager.getConversation(player)
        if (conversation == null) {
            val message = Component.text("You are not currently in a conversation. Use /ttv to start one.")
                .decorate(TextDecoration.ITALIC)

            player.sendMessage(ChatMessageTemplate.withPluginNamePrefix(message))
            return true
        }

        plugin.conversationManager.endConversation(conversation)
        return true
    }
}