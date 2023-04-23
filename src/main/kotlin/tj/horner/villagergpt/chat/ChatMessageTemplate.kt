package tj.horner.villagergpt.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

object ChatMessageTemplate {
    fun withPluginNamePrefix(component: Component): Component {
        return Component.text().content("")
            .append(Component.text("[VillagerGPT] ").color(NamedTextColor.GREEN))
            .append(component)
            .build()
    }
}