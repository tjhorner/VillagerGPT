package tj.horner.villagergpt.conversation.pipeline.actions

import org.bukkit.entity.Villager
import org.bukkit.inventory.MerchantRecipe
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageAction

class SetTradesAction(private val villager: Villager, private val trades: List<MerchantRecipe>) : ConversationMessageAction {
    override fun run() {
        villager.resetOffers()
        villager.recipes = trades
    }
}