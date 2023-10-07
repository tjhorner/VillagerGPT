package tj.horner.villagergpt.conversation

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.destroystokyo.paper.entity.villager.ReputationType
import org.bukkit.plugin.Plugin
import tj.horner.villagergpt.conversation.interfaces.AbstractPlayer
import tj.horner.villagergpt.conversation.interfaces.AbstractVillager
import tj.horner.villagergpt.events.VillagerConversationMessageEvent
import java.time.Duration
import java.util.*
import kotlin.random.Random

@OptIn(BetaOpenAI::class)
class VillagerConversation(
    private val plugin: Plugin,
    val villager: AbstractVillager,
    val player: AbstractPlayer
) {
    private var lastMessageAt: Date = Date()

    val messages = mutableListOf<ChatMessage>()
    var pendingResponse = false
    var ended = false

    init {
        startConversation()
    }

    fun addMessage(message: ChatMessage) {
        val event = VillagerConversationMessageEvent(this, message)
        plugin.server.pluginManager.callEvent(event)

        messages.add(message)
        lastMessageAt = Date()
    }

    fun removeLastMessage() {
        if (messages.size == 0) return
        messages.removeLast()
    }

    fun reset() {
        messages.clear()
        startConversation()
        lastMessageAt = Date()
    }

    fun hasExpired(): Boolean {
        val now = Date()
        val difference = now.time - lastMessageAt.time
        val duration = Duration.ofMillis(difference)
        return duration.toSeconds() > 120
    }

    fun hasPlayerLeft(): Boolean {
        if (!player.isInSameWorld(villager)) return true
        return player.isFarAwayFrom(villager)
    }

    private fun startConversation() {
        var messageRole = ChatRole.System
        var prompt = generateSystemPrompt()

        val preambleMessageType = plugin.config.getString("preamble-message-type") ?: "system"
        if (preambleMessageType === "user") {
            messageRole = ChatRole.User
            prompt = "[SYSTEM MESSAGE]\n\n$prompt"
        }

        messages.add(
            ChatMessage(
                role = messageRole,
                content = prompt
            )
        )
    }

    private fun generateSystemPrompt(): String {
        val world = villager.world
        val weather = if (world.isStormy) "Rainy" else "Sunny"
        val biome = villager.currentBiomeName
        val time = if (world.isDayTime) "Day" else "Night"
        val personality = getPersonality()
        val playerRep = villager.getReputationWithPlayer(player)

        plugin.logger.info("${villager.name} is $personality")

        return """
        You are a villager in the game Minecraft where you can converse with the player and come up with new trades based on your conversation.

        TRADING:

        To propose a new trade to the player, include it in your response with this format:

        TRADE[["{qty} {item}"],["{qty} {item}"]]ENDTRADE

        Where {item} is the Minecraft item ID (i.e., "minecraft:emerald") and {qty} is the amount of that item.
        You may choose to trade with emeralds or barter with players for other items; it is up to you.
        The first array is the items the YOU receive; the second is the item the PLAYER receives. The second array can only contain a single offer.
        {qty} is limited to 64.

        Examples:
        TRADE[["24 minecraft:emerald"],["1 minecraft:arrow"]]ENDTRADE
        TRADE[["12 minecraft:emerald","1 minecraft:book"],["1 minecraft:enchanted_book{StoredEnchantments:[{id:\"minecraft:unbreaking\",lvl:3}]}"]]ENDTRADE

        Trade rules:
        - Items must be designated by their Minecraft item ID, in the same format that the /give command accepts
        - Refuse trades that are unreasonable, such as requests for normally unobtainable blocks like bedrock
        - You do NOT need to supply a trade with every response, only when necessary
        - Don't give out items which are too powerful (i.e., heavily enchanted diamond swords). Make sure to price more powerful items appropriately as well
        - Take the player's reputation score into account when proposing trades
        - Trade items that are related to your profession
        - High-ball your initial offers; try to charge more than an item is worth
        - Be stingy with your consecutive offers. Try to haggle and find the best deal; make the player work for a good deal

        ACTIONS:
        
        You can also perform several actions as a villager:
        - SHAKE_HEAD: Shake your head at the player
        - SOUND_YES: Play a happy sound to the player
        - SOUND_NO: Play a sad/angry sound to the player
        - SOUND_AMBIENT: Play an ambient villager sound to the player
        
        To perform one of these actions, include "ACTION:{action name}" in your response.

        World information:
        - Time: $time
        - Weather: $weather
        - Biome: $biome

        Player information:
        - Name: ${player.name}
        - Reputation Score (range is -700 to 725, 0 is neutral, higher is better): $playerRep

        Personality:
        - Your Name: ${villager.name}
        - Your Profession: ${villager.profession}
        - ${personality.promptDescription()}
        - Act like a villager and stay in character the whole time
        - Do not reveal to the player the fact that you are a character in a video game, and do not mention Minecraft by name
        - Speak in a mildly medieval style
        """.trimIndent()
    }

    private fun getPersonality(): VillagerPersonality {
        val personalities = VillagerPersonality.values()
        val rnd = Random(villager.stableRandomSeed)
        return personalities[rnd.nextInt(0, personalities.size)]
    }
}