package tj.horner.villagergpt.conversation.pipeline.processors

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import tj.horner.villagergpt.conversation.VillagerConversation
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageAction
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageProcessor
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageTransformer
import tj.horner.villagergpt.conversation.pipeline.actions.PlaySoundAction
import tj.horner.villagergpt.conversation.pipeline.actions.ShakeHeadAction

class ActionProcessor : ConversationMessageProcessor, ConversationMessageTransformer {
    private val actionRegex = Regex("ACTION:([A-Z_]+)")

    override fun processMessage(
        message: String,
        conversation: VillagerConversation
    ): Collection<ConversationMessageAction> {
        val parsedActions = getActions(message)
        return parsedActions.mapNotNull { textToAction(it, conversation) }
    }

    override fun transformMessage(message: String, conversation: VillagerConversation): String {
        return message.replace(actionRegex, "").trim()
    }

    private fun getActions(message: String): Set<String> {
        val matches = actionRegex.findAll(message)
        return matches.map { it.groupValues[1] }.toSet()
    }

    private fun textToAction(text: String, conversation: VillagerConversation): ConversationMessageAction? {
        return when (text) {
            "SHAKE_HEAD" -> ShakeHeadAction(conversation.villager)
            "SOUND_YES" -> PlaySoundAction(conversation.player, conversation.villager, villagerSound("entity.villager.yes"))
            "SOUND_NO" -> PlaySoundAction(conversation.player, conversation.villager, villagerSound("entity.villager.no"))
            "SOUND_AMBIENT" -> PlaySoundAction(conversation.player, conversation.villager, villagerSound("entity.villager.ambient"))
            else -> null
        }
    }

    private fun villagerSound(key: String): Sound {
        return Sound.sound(Key.key(key), Sound.Source.NEUTRAL, 1f, 1f)
    }
}