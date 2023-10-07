package tj.horner.villagergpt.conversation.dto

data class TradeOfferDto(
    val ingredients: List<TradeItemDto>,
    val results: List<TradeItemDto>
)
