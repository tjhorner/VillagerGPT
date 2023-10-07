package tj.horner.villagergpt.bukkit.conversation

import com.destroystokyo.paper.entity.villager.ReputationType
import org.bukkit.Material
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import tj.horner.villagergpt.conversation.interfaces.AbstractPlayer
import tj.horner.villagergpt.conversation.interfaces.AbstractVillager

class BukkitVillager(val villager: Villager) : BukkitEntity(villager), AbstractVillager {
    override val profession: String
        get() = villager.profession.name

    override fun getReputationWithPlayer(player: AbstractPlayer): Int {
        val bukkitPlayer = (player as BukkitPlayer).player

        var finalScore: Int = 0
        val rep = villager.getReputation(bukkitPlayer.uniqueId) ?: return 0

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

    private fun parseItemStack(text: String): ItemStack {
        val matches = Regex("([0-9]+) (.+)").find(text) ?: return ItemStack(Material.AIR)

        val (numItems, materialString) = matches.destructured
        val stack = itemFactory.createItemStack(materialString)
        stack.amount = numItems.toInt().coerceAtMost(64)

        return stack
    }
}