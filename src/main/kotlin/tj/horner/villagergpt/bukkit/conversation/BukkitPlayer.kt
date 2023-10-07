package tj.horner.villagergpt.bukkit.conversation

import org.bukkit.entity.Player
import tj.horner.villagergpt.conversation.interfaces.AbstractEntity
import tj.horner.villagergpt.conversation.interfaces.AbstractPlayer

class BukkitPlayer(val player: Player) : BukkitEntity(player), AbstractPlayer {
}