package tj.horner.villagergpt.conversation.pipeline.actions

import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import tj.horner.villagergpt.conversation.interfaces.AbstractEntity
import tj.horner.villagergpt.conversation.interfaces.AbstractPlayer
import tj.horner.villagergpt.conversation.pipeline.ConversationMessageAction

class PlaySoundAction(private val player: AbstractPlayer, private val entity: AbstractEntity, private val sound: String) : ConversationMessageAction {
    override fun run() {
        player.playSound(sound, entity)
    }
}