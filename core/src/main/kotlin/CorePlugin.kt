package com.runerealms.core

import com.runerealms.core.config.CoreConfig
import com.runerealms.core.config.createLocale
import com.runerealms.core.config.hoconConfig
import com.runerealms.core.feature.RuneFeature
import com.runerealms.core.locale.PluginLocale
import org.bukkit.plugin.java.JavaPlugin
import kotlin.properties.Delegates

public class CorePlugin: RunePlugin() {
    @PublishedApi
    internal val globalFeatureData: MutableMap<String, Any> = mutableMapOf()
    internal val globallyInstalledFeatures = mutableListOf<RuneFeature<*>>()

    public var config: CoreConfig by Delegates.notNull()

    override fun onStart() {
        config = hoconConfig("core", CoreConfig.Default)
        makeLocale()
    }

    public inline fun <reified T : Any> getGlobalFeatureDataset(key: String): T? {
        return globalFeatureData[key] as? T
    }

    private fun makeLocale() {
        createLocale("messages") {
            this["commands.invalid-syntax"] = "&c&lERRO &fA sintaxe do comando está incorreta. Use &e{0}&f."
        }
    }

    public companion object {
        @JvmStatic
        public val instance: CorePlugin get() = getPlugin(CorePlugin::class.java)
    }
}