package com.runerealms.core

import com.runerealms.core.config.CoreConfig
import com.runerealms.core.config.hoconConfig
import com.runerealms.core.feature.RuneFeature

public class CorePlugin: RunePlugin() {
    @PublishedApi
    internal val globalFeatureData: MutableMap<String, Any> = mutableMapOf()
    internal val globallyInstalledFeatures = mutableListOf<RuneFeature<*>>()

    public lateinit var config: CoreConfig

    init {
        instance = this
    }

    override fun onStart() {
        config = hoconConfig("core", CoreConfig.Default)
    }

    public inline fun <reified T : Any> getGlobalFeatureDataset(key: String): T? {
        return globalFeatureData[key] as? T
    }

    public companion object {
        public lateinit var instance: CorePlugin
    }
}