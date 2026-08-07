package tj.horner.villagergpt.conversation.tools

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import tj.horner.villagergpt.conversation.VillagerConversation
import tj.horner.villagergpt.conversation.pipeline.actions.SendPlayerMessageAction
import tj.horner.villagergpt.conversation.pipeline.actions.SetTradesAction
import java.util.logging.Level
import java.util.logging.Logger

class ProposeTradeTool(private val logger: Logger) : ConversationTool {
    override val name = "propose_trade"
    override val description = "Offer the player one or more trades. Replaces any trades you previously offered them."

    private fun itemStackSchema(description: String) = """
        {
          "type": "object",
          "description": "$description",
          "properties": {
            "item": {
              "type": "string",
              "description": "Minecraft item ID, in the same format the /give command accepts (e.g. minecraft:emerald)"
            },
            "quantity": {
              "type": "integer",
              "description": "Amount of the item, from 1 to 64",
              "minimum": 1,
              "maximum": 64
            }
          },
          "required": ["item", "quantity"]
        }
    """.trimIndent()

    override val parameters = JsonParser.parseString(
        """
        {
          "type": "object",
          "properties": {
            "trades": {
              "type": "array",
              "description": "One or more trades to offer the player at once.",
              "minItems": 1,
              "maxItems": 5,
              "items": {
                "type": "object",
                "properties": {
                  "villager_receives": {
                    "type": "array",
                    "description": "The item(s) the player must pay, in order to receive player_receives in return. 1 or 2 item stacks.",
                    "minItems": 1,
                    "maxItems": 2,
                    "items": ${itemStackSchema("An item stack the player must pay")}
                  },
                  "player_receives": ${itemStackSchema("The single item stack the player receives in the trade")}
                },
                "required": ["villager_receives", "player_receives"]
              }
            }
          },
          "required": ["trades"]
        }
        """.trimIndent()
    ).asJsonObject

    private data class ItemStackSpec(val item: String?, val quantity: Int?)

    private data class TradeSpec(
        @SerializedName("villager_receives") val villagerReceives: List<ItemStackSpec>?,
        @SerializedName("player_receives") val playerReceives: ItemStackSpec?
    )

    private data class Arguments(val trades: List<TradeSpec>?)

    private val gson = Gson()
    private val itemFactory = Bukkit.getServer().itemFactory

    override fun execute(argumentsJson: String, conversation: VillagerConversation): ToolCallResult {
        val arguments = try {
            gson.fromJson(argumentsJson, Arguments::class.java)
        } catch (e: JsonSyntaxException) {
            return ToolCallResult("Error: could not parse arguments: ${e.message}")
        }

        val recipes = try {
            buildRecipes(arguments)
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Villager proposed invalid trades: $argumentsJson", e)
            return ToolCallResult("Error: ${e.message}")
        }

        val formattedMessage = formatTradesMessage(conversation.villager, recipes)

        return ToolCallResult(
            resultForModel = "Offered ${recipes.size} trade(s) to the player.",
            actions = listOf(
                SetTradesAction(conversation.villager, recipes),
                SendPlayerMessageAction(conversation.player, formattedMessage)
            )
        )
    }

    private fun buildRecipes(arguments: Arguments): List<MerchantRecipe> {
        val trades = arguments.trades
        require(!trades.isNullOrEmpty()) { "trades must contain at least one trade" }

        return trades.map { buildRecipe(it) }
    }

    private fun buildRecipe(trade: TradeSpec): MerchantRecipe {
        val villagerReceives = trade.villagerReceives
        require(!villagerReceives.isNullOrEmpty()) { "villager_receives must contain at least one item" }
        require(villagerReceives.size <= 2) { "villager_receives can contain at most two items" }

        val playerReceivesSpec = requireNotNull(trade.playerReceives) { "player_receives is required" }

        val ingredients = villagerReceives.map { parseItemStack(it) }
        val result = parseItemStack(playerReceivesSpec)

        val recipe = MerchantRecipe(result, 1)
        recipe.ingredients = ingredients

        return recipe
    }

    private fun parseItemStack(spec: ItemStackSpec): ItemStack {
        val itemId = spec.item?.trim()
        require(!itemId.isNullOrEmpty()) { "an item stack is missing its item ID" }

        val stack = itemFactory.createItemStack(itemId)
        stack.amount = (spec.quantity ?: 1).coerceIn(1, 64)

        return stack
    }

    private fun formatTradesMessage(villager: Villager, recipes: List<MerchantRecipe>): Component {
        val label = if (recipes.size == 1) " has proposed a trade:" else " has proposed trades:"

        val header = Component.text()
            .append(villager.name().color(NamedTextColor.AQUA))
            .append(Component.text(label))
            .build()

        val trades = Component.join(
            JoinConfiguration.separator(Component.newline()),
            recipes.map { chatFormattedRecipe(it) }
        )

        return Component.text().append(header).appendNewline().append(trades).build()
    }

    private fun chatFormattedRecipe(recipe: MerchantRecipe): Component {
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
}
