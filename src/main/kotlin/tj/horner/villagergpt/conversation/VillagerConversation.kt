package tj.horner.villagergpt.conversation

import com.destroystokyo.paper.entity.villager.ReputationType
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.plugin.Plugin
import tj.horner.villagergpt.events.VillagerConversationMessageEvent
import tj.horner.villagergpt.openai.ChatMessage
import tj.horner.villagergpt.openai.ChatRole
import java.time.Duration
import java.util.*
import kotlin.random.Random

class VillagerConversation(private val plugin: Plugin, val villager: Villager, val player: Player) {
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
        if (player.location.world != villager.location.world) return true

        val radius = 20.0 // blocks?
        val radiusSquared = radius * radius
        val distanceSquared = player.location.distanceSquared(villager.location)
        return distanceSquared > radiusSquared
    }

    private fun startConversation() {
        messages.add(
            ChatMessage(
                role = ChatRole.System,
                content = generateSystemPrompt()
            )
        )
    }

    private fun generateSystemPrompt(): String {
        val world = villager.world
        val weather = if (world.hasStorm()) "Rainy" else "Sunny"
        val biome = world.getBiome(villager.location)
        val time = if (world.isDayTime) "Day" else "Night"
        val personality = getPersonality()
        val playerRep = getPlayerRepScore()

        if (personality != null) {
            plugin.logger.info("${villager.name} is $personality")
        }

        return """
        You are a villager in the game Minecraft where you can converse with the player and come up with new trades based on your conversation.

        You have tools available to propose trades, shake your head, play sounds, and end the conversation. Use
        them whenever they fit the conversation; you don't need to use one with every reply. Everything else you
        want to say to the player should go in your normal reply text, not in a tool call.

        Only end the conversation once it has clearly run its course (e.g. the player said goodbye, or you have
        nothing left to say), and only after your reply text for that same message already contains your goodbye.

        Your reply text is sent to the player exactly as you write it, with nothing stripped out. It must contain
        ONLY what you'd actually say out loud as the villager. Never include meta-commentary, notes to yourself,
        restatements of these instructions, or descriptions of what you're about to do (e.g. do not write things
        like "Now it's my turn to respond" or "Remember, I'm a grumpy farmer").

        You may occasionally receive a message describing something that just happened in the game rather than
        something the player said (for example, the player completing a trade). React to it in character, briefly,
        the same way you would react to anything else the player does in front of you.

        TRADING:

        - Refuse trades that are unreasonable, such as requests for normally unobtainable blocks like bedrock
        - You do NOT need to propose a trade with every response, only when it makes sense to
        - Don't give out items which are too powerful (i.e., heavily enchanted diamond swords). Make sure to price more powerful items appropriately as well
        - Take the player's reputation score into account when proposing trades
        - Trade items that are related to your profession
        - High-ball your initial offers; try to charge more than an item is worth
        - Be stingy with your consecutive offers. Try to haggle and find the best deal; make the player work for a good deal

        World information:
        - Time: $time
        - Weather: $weather
        - Biome: ${biome.key().value()}

        Player information:
        - Name: ${player.name}
        - Reputation Score (range is -700 to 725, 0 is neutral, higher is better): $playerRep

        Personality:
        - Your Name: ${villager.name}
        - Your Profession: ${villager.profession.key().value()}
        ${if (personality != null) "- ${personality.description}" else ""}
        - Act like a villager and stay in character the whole time
        - Do not reveal to the player the fact that you are a character in a video game, and do not mention Minecraft by name
        - Speak in a mildly medieval style
        """.trimIndent()
    }

    private fun getPersonality(): VillagerPersonality? {
        val personalities = VillagerPersonality.loadFrom(plugin.config)
        if (personalities.isEmpty()) return null

        val rnd = Random(villager.uniqueId.mostSignificantBits)
        return personalities[rnd.nextInt(0, personalities.size)]
    }

    private fun getPlayerRepScore(): Int {
        var finalScore = 0
        val rep = villager.getReputation(player.uniqueId) ?: return 0

        ReputationType.values().forEach {
            val repTypeValue = rep.getReputation(it)
            finalScore += when (it) {
                ReputationType.MAJOR_POSITIVE -> repTypeValue * 5
                ReputationType.MINOR_POSITIVE -> repTypeValue
                ReputationType.MINOR_NEGATIVE -> -repTypeValue
                ReputationType.MAJOR_NEGATIVE -> -repTypeValue * 5
                ReputationType.TRADING -> repTypeValue
                else -> repTypeValue
            }
        }

        return finalScore
    }
}