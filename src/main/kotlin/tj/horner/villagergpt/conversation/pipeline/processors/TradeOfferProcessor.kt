package tj.horner.villagergpt.conversation.pipeline.processors

import com.google.gson.Gson
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import tj.horner.villagergpt.conversation.VillagerConversation
import tj.horner.villagergpt.conversation.formatting.MessageFormatter
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageAction
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageProcessor
import tj.horner.villagergpt.conversation.pipeline.actions.SendPlayerMessageAction
import tj.horner.villagergpt.conversation.pipeline.actions.SetTradesAction
import java.util.logging.Level
import java.util.logging.Logger

class TradeOfferProcessor(private val logger: Logger) : ConversationMessageProcessor {
    private val gson = Gson()
    private val itemFactory = Bukkit.getServer().itemFactory

    override fun processMessage(message: String, conversation: VillagerConversation): Collection<ConversationMessageAction> {
        val tradeExpressionRegex = Regex("TRADE(\\[.+?\\])ENDTRADE")
        val splitMessage = splitWithMatches(message, tradeExpressionRegex)

        val trades = mutableListOf<MerchantRecipe>()

        val messageComponent = Component.text().content("")

        splitMessage.forEach {
            if (it.trim().startsWith("TRADE")) {
                val response = it.trim().replace(Regex("(^TRADE)|(ENDTRADE$)"), "")

                try {
                    val trade = parseTradeResponse(response)
                    trades.add(trade)

                    val tradeMessage = chatFormattedRecipe(trade)
                    messageComponent.append(tradeMessage)
                } catch(e: Exception) {
                    logger.log(Level.WARNING, "Chat response contained invalid trade: $response", e)
                    messageComponent.append(invalidTradeComponent(response))
                }
            } else {
                messageComponent.append(Component.text(it).color(NamedTextColor.WHITE))
            }
        }

        val formattedMessage = MessageFormatter.formatMessageFromVillager(messageComponent.build(), conversation.villager)

        return listOf(
            SetTradesAction(conversation.villager, trades),
            SendPlayerMessageAction(conversation.player, formattedMessage)
        )
    }

    private fun parseTradeResponse(text: String): MerchantRecipe {
        val tradeList = gson.fromJson(text, arrayListOf(arrayListOf<String>()).javaClass)

        val ingredients = tradeList[0].map { parseItemStack(it) }
        val results = tradeList[1].map { parseItemStack(it) }

        val recipe = MerchantRecipe(results[0], 1)
        recipe.ingredients = ingredients

        return recipe
    }

    private fun parseItemStack(text: String): ItemStack {
        val matches = Regex("([0-9]+) (.+)").find(text) ?: return ItemStack(Material.AIR)

        val (numItems, materialString) = matches.destructured
        val stack = itemFactory.createItemStack(materialString)
        stack.amount = numItems.toInt().coerceAtMost(64)

        return stack
    }

    private fun chatFormattedRecipe(recipe: MerchantRecipe): TextComponent {
        val component = Component.text().content("[")

        recipe.ingredients.forEachIndexed { index, it ->
            component.append(Component.text("${it.amount} ").color(NamedTextColor.LIGHT_PURPLE))
            component.append(it.displayName())

            if (index + 1 < recipe.ingredients.count())
                component.append(Component.text(" + "))
            else
                component.append(Component.text(" "))
        }

        component.append(Component.text("→ "))
        component.append(Component.text("${recipe.result.amount} ").color(NamedTextColor.LIGHT_PURPLE))
        component.append(recipe.result.displayName())
        component.append(Component.text("]"))

        component.color(NamedTextColor.DARK_GREEN)

        return component.build()
    }

    private fun invalidTradeComponent(rawTrade: String): Component {
        return Component.text()
            .content("[Invalid Trade]")
            .hoverEvent(HoverEvent.showText(Component.text("The response contained a recipe for an invalid trade. Here is the attempted recipe:\n\n$rawTrade")))
            .color(NamedTextColor.RED)
            .build()
    }

    private fun splitWithMatches(input: String, regex: Regex): List<String> {
        val result = mutableListOf<String>()
        var lastIndex = 0

        regex.findAll(input).forEach { match ->
            result.add(input.subSequence(lastIndex, match.range.first).toString())
            result.add(match.value)
            lastIndex = match.range.last + 1
        }

        if (lastIndex < input.length) {
            result.add(input.subSequence(lastIndex, input.length).toString())
        }

        return result
    }
}