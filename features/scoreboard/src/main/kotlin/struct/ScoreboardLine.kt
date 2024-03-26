package com.runerealms.core.feature.scoreboard.struct

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

public class ScoreboardLine(
    public val provider: (Player) -> List<Component>,
    public var customUpdateTickInterval: Int? = null
) {
    internal val internalCache = mutableMapOf<Player, Component>()

    public class Builder {
        public val components: MutableList<Component> = mutableListOf()
        private var rule: (Component) -> Component = { it }

        public operator fun Component.unaryPlus() {
            components.add(rule(this))
        }

        public operator fun String.unaryPlus() {
            components.add(rule(Component.text(this)))
        }

        public fun rule(rule: (Component) -> Component) {
            this.rule = rule
        }
    }
}