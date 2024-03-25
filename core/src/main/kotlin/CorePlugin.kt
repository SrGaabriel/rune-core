package com.runerealms.core

import com.runerealms.core.feature.RuneFeature

public class CorePlugin: RunePlugin() {
    @PublishedApi
    internal val globalFeatureData: MutableMap<String, Any> = mutableMapOf()
    internal val globallyInstalledFeatures = mutableListOf<RuneFeature<*>>()

    override fun onStart() {

    }

    public inline fun <reified T : Any> getGlobalFeatureDataset(key: String): T? {
        return globalFeatureData[key] as? T
    }

    public companion object {
        public val instance: CorePlugin get() = getPlugin(CorePlugin::class.java)
    }
}