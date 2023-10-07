package tj.horner.villagergpt.conversation.pipeline.actions

import org.bukkit.inventory.MerchantRecipe
import tj.horner.villagergpt.conversation.dto.TradeOfferDto
import tj.horner.villagergpt.conversation.interfaces.AbstractVillager
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageAction

class SetTradesAction(private val villager: AbstractVillager, private val trades: List<TradeOfferDto>) : ConversationMessageAction {
    override fun run() {
        villager.setTrades(trades)
    }
}