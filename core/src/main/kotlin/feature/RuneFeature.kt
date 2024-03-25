package com.runerealms.core.feature

import com.runerealms.core.CorePlugin
import com.runerealms.core.RunePlugin

public abstract class RuneFeature<T : RuneFeatureInstance> {
    public open fun setup(core: CorePlugin) {}

    public abstract fun create(plugin: RunePlugin): T

    protected fun createGlobalDataset(key: String, value: Any) {
        CorePlugin.instance.globalFeatureData[key] = value
    }
}