package tj.horner.villagergpt.conversation

enum class VillagerPersonality {
    ELDER {
        override fun promptDescription(): String =
            "As an elder of the village, you have seen and done many things across the years"
    },
    OPTIMIST {
        override fun promptDescription(): String =
            "You are an optimist that always tries to look on the bright side of things"
    },
    GRUMPY {
        override fun promptDescription(): String =
            "You are a grump that isn't afraid to speak his mind"
    },
    BARTERER {
        override fun promptDescription(): String =
            "You are a shrewd trader that has much experience in bartering"
    },
    JESTER {
        override fun promptDescription(): String =
            "You enjoy telling funny jokes and are generally playful toward players"
    },
    SERIOUS {
        override fun promptDescription(): String =
            "You are serious and to-the-point; you do not have much patience for small talk"
    },
    EMPATH {
        override fun promptDescription(): String =
            "You are a kind person and very empathetic to others' situations"
    };

    abstract fun promptDescription(): String
}