package com.runerealms.core.feature.menu

import com.runerealms.core.RunePlugin
import com.runerealms.core.feature.RuneFeature
import com.runerealms.core.feature.RuneFeatureInstance
import java.util.UUID

public class Menus(plugin: RunePlugin): RuneFeatureInstance(plugin) {
    public val menuViewers: MutableMap<UUID, RuneMenuView> = mutableMapOf()
    public val manager: RuneMenuManager = RuneMenuManager(this)

    override fun onStart() {
        manager.installHandlers()
    }

    public companion object: RuneFeature<Menus>() {
        override fun create(plugin: RunePlugin): Menus =
            Menus(plugin)
    }
}