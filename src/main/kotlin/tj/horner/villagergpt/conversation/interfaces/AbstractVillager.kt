package tj.horner.villagergpt.conversation.interfaces

import tj.horner.villagergpt.conversation.dto.TradeOfferDto

interface AbstractVillager : AbstractEntity {
    val profession: String

    fun getReputationWithPlayer(player: AbstractPlayer): Int
    fun setTrades(trades: List<TradeOfferDto>)
}