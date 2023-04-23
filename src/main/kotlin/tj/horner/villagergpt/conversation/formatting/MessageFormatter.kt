package tj.horner.villagergpt.conversation.formatting

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Villager

object MessageFormatter {
    fun formatMessageFromPlayer(message: Component, villager: Villager): Component {
        return formatMessage(message, playerComponent(), villagerComponent(villager))
    }

    fun formatMessageFromVillager(message: Component, villager: Villager): Component {
        return formatMessage(message, villagerComponent(villager), playerComponent())
    }

    private fun formatMessage(message: Component, sender: Component, recipient: Component): Component {
        val formattedMessage = Component.text().content("")

        return formattedMessage
            .append(sender)
            .append(Component.text(" → ").color(NamedTextColor.WHITE))
            .append(recipient)
            .append(Component.text(": "))
            .append(message)
            .build()
    }

    private fun playerComponent(): Component {
        return Component.text("You").color(NamedTextColor.DARK_AQUA)
    }

    private fun villagerComponent(villager: Villager): Component {
        return villager.name().color(NamedTextColor.AQUA)
    }
}