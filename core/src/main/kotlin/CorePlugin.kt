package com.runerealms.core

import com.runerealms.core.config.CoreConfig
import com.runerealms.core.config.hoconConfig
import com.runerealms.core.feature.RuneFeature
import org.bukkit.plugin.java.JavaPlugin
import kotlin.properties.Delegates

public class CorePlugin: RunePlugin() {
    @PublishedApi
    internal val globalFeatureData: MutableMap<String, Any> = mutableMapOf()
    internal val globallyInstalledFeatures = mutableListOf<RuneFeature<*>>()

    public var config: CoreConfig by Delegates.notNull()

    override fun onStart() {
        config = hoconConfig("core", CoreConfig.Default)
    }

    public inline fun <reified T : Any> getGlobalFeatureDataset(key: String): T? {
        return globalFeatureData[key] as? T
    }

    public companion object {
        @JvmStatic
        public val instance: CorePlugin get() = getPlugin(CorePlugin::class.java)
    }
}