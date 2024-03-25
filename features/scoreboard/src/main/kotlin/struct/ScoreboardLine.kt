package com.runerealms.core.feature.scoreboard.struct

import net.kyori.adventure.text.Component

public class ScoreboardLine(public var customUpdateTickInterval: Int? = null) {
    public val components: MutableList<Component> = mutableListOf()

    public operator fun Component.unaryPlus() {
        components.add(this)
    }

    public operator fun String.unaryPlus() {
        components.add(Component.text(this))
    }
}