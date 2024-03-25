package com.runerealms.core.feature.scoreboard

import com.runerealms.core.RunePlugin
import com.runerealms.core.feature.scoreboard.struct.Scoreboard
import net.kyori.adventure.text.Component

public fun RunePlugin.scoreboard(
    id: String,
    init: Scoreboard.() -> Unit
): Scoreboard {
    val scoreboard = Scoreboard(this, id).apply(init)
    feature(Scoreboards).register(scoreboard)
    return scoreboard
}