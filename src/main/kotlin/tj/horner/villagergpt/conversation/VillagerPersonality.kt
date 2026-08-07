package tj.horner.villagergpt.conversation

import org.bukkit.configuration.Configuration

data class VillagerPersonality(val name: String, val description: String) {
    override fun toString() = name

    companion object {
        /** Reads the `personalities` list from config.yml. Empty if it's missing or every entry is malformed. */
        fun loadFrom(config: Configuration): List<VillagerPersonality> {
            return config.getMapList("personalities").mapNotNull { entry ->
                val name = entry["name"] as? String ?: return@mapNotNull null
                val description = entry["description"] as? String ?: return@mapNotNull null
                VillagerPersonality(name, description)
            }
        }
    }
}
