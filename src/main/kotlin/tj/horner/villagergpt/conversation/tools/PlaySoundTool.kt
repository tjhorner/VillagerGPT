package tj.horner.villagergpt.conversation.tools

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import tj.horner.villagergpt.conversation.VillagerConversation
import tj.horner.villagergpt.conversation.pipeline.actions.PlaySoundAction

class PlaySoundTool : ConversationTool {
    override val name = "play_sound"
    override val description = "Play a short sound to express how you're feeling toward the player"

    override val parameters = JsonParser.parseString(
        """
        {
          "type": "object",
          "properties": {
            "sound": {
              "type": "string",
              "description": "Which sound to play",
              "enum": ["happy", "sad", "ambient"]
            }
          },
          "required": ["sound"]
        }
        """.trimIndent()
    ).asJsonObject

    private data class Arguments(val sound: String?)

    private val gson = Gson()

    override fun execute(argumentsJson: String, conversation: VillagerConversation): ToolCallResult {
        val arguments = try {
            gson.fromJson(argumentsJson, Arguments::class.java)
        } catch (e: JsonSyntaxException) {
            return ToolCallResult("Error: could not parse arguments: ${e.message}")
        }

        val soundKey = when (arguments?.sound) {
            "happy" -> "entity.villager.yes"
            "sad" -> "entity.villager.no"
            "ambient" -> "entity.villager.ambient"
            else -> return ToolCallResult("Error: '${arguments?.sound}' is not a valid sound. Must be one of: happy, sad, ambient")
        }

        val sound = Sound.sound(Key.key(soundKey), Sound.Source.NEUTRAL, 1f, 1f)

        return ToolCallResult(
            resultForModel = "Played the '${arguments.sound}' sound.",
            actions = listOf(PlaySoundAction(conversation.player, conversation.villager, sound))
        )
    }
}
